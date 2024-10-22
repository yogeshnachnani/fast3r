package supercr.workflows.codereview.components

import Editor
import Grid
import MouseEvent
import codereview.FileDiffV2
import codereview.getNewFileText
import codereview.getOldFileText
import codereview.getUniqueIdentifier
import codereview.hasNewFile
import codereview.hasOldFile
import kotlinx.css.LinearDimension
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.styledDiv
import styled.css
import kotlinx.css.Position
import kotlinx.css.left
import kotlinx.css.position
import kotlinx.css.px
import kotlinx.css.top
import react.setState
import styled.getClassName
import supercr.css.ComponentStyles
import supercr.css.GutterDecorationStyles
import supercr.workflows.codereview.processor.TextDiffProcessor
import supercr.css.commentBoxWidth
import supercr.kb.UniversalKeyboardShortcutHandler
import supercr.kb.DiffViewShortcuts
import codereview.FileCommentHandler
import supercr.kb.DiffViewShortcuts.NextHunk
import supercr.kb.DiffViewShortcuts.PreviousHunk
import supercr.workflows.codereview.processor.hasBothFiles
import supercr.workflows.codereview.processor.nextEditPosition
import kotlin.js.Date
import kotlin.math.max
import kotlin.math.min

external interface DiffViewProps: RProps {
    var fileDiff: FileDiffV2
    var identifier: String
    var oldFileNewCommentHandler: FileCommentHandler
    var newFileNewCommentHandler: FileCommentHandler
    var defaultActionBarActions: List<ActionBarShortcut>
}
external interface DiffViewState: RState {
    var currentCommentBoxRowPosition: Int?
    /** The index of the current Hunk (or Edit) in fileDiff.editList */
    var currentHunkIndex: Int
}

class AceCommentsWrapper(
    private var editor: Editor,
    val commentBoxXPosition: () -> LinearDimension,
    val commentHandler: FileCommentHandler
) {
    fun highlightOldCommentLines() {
        commentHandler.existingComments.keys.highlightOldCommentLines(editor)
    }

    fun highlightNewCommentLines() {
        commentHandler.newComments.keys.highlightOldCommentLines(editor)
    }

    fun isCommentAllowed(): Boolean {
        /** TODO: implement. One way to check for existing comments is : oldComments.containsKey(documentPosition.row) */
        return true
    }

    fun convertToScreenCoordinates(row: Int): Pair<Number, Number> {
        val screenPosition = editor.renderer.textToScreenCoordinates(row.toDouble(), 0.toDouble())
        return Pair(screenPosition.pageX, screenPosition.pageY)
    }

    private fun Set<Int>.highlightOldCommentLines(forEditor: Editor) {
        this
            .map { viewPosition ->
                forEditor.getSession().addGutterDecoration(viewPosition, GutterDecorationStyles.commentIcon)
            }
    }

}

const val LEFT_EDITOR_DIV_ID = "fast3r-left-view"
const val RIGHT_EDITOR_DIV_ID = "fast3r-right-view"

class DiffView: RComponent<DiffViewProps, DiffViewState>() {
    val ace = js("require('ace-builds/src-noconflict/ace')")
    private var leftEditorCommentsWrapper: AceCommentsWrapper? = null
    private var rightEditorCommentsWrapper: AceCommentsWrapper? = null
    private var leftEditor: Editor? = null
    private var rightEditor: Editor? = null
    private lateinit var editorForCurrentComment: AceCommentsWrapper

    override fun DiffViewState.init() {
        currentCommentBoxRowPosition = null
        currentHunkIndex = -1
    }

    override fun RBuilder.render() {
        Grid {
            attrs {
                item = false
                container = true
                direction = "row"
                spacing = 0
            }
            if (props.fileDiff.hasOldFile()) {
                Grid {
                    attrs {
                        item = true
                        container = false
                        md = calculateMdWidth()
                    }
                    codeView {
                        attrs {
                            id = leftSideTextId()
                            fileText = props.fileDiff.getOldFileText()
                            divId = LEFT_EDITOR_DIV_ID
                            actionBarActions = if (!props.fileDiff.hasNewFile()) {
                                props.defaultActionBarActions
                            } else {
                                emptyList()
                            }
                        }
                    }
                }
            }
            if (props.fileDiff.hasNewFile()) {
                Grid {
                    attrs {
                        item = true
                        container = false
                        md = calculateMdWidth()
                    }
                    codeView {
                        attrs {
                            id = rightSideTextId()
                            fileText = props.fileDiff.getNewFileText()
                            divId = RIGHT_EDITOR_DIV_ID
                            actionBarActions = props.defaultActionBarActions.plus(additionalShortcutsForTwoPaneView)
                        }
                    }
                }
            }
            if (state.currentCommentBoxRowPosition != null) {
                renderCommentBox()
            }
        }
    }

    override fun getSnapshotBeforeUpdate(prevProps: DiffViewProps, prevState: DiffViewState): Any {
        return prevProps.identifier != props.identifier
    }

    override fun shouldComponentUpdate(nextProps: DiffViewProps, nextState: DiffViewState): Boolean {
        return (nextProps.identifier != props.identifier) || (nextState.currentCommentBoxRowPosition != state.currentCommentBoxRowPosition)
    }

    override fun componentDidUpdate(prevProps: DiffViewProps, prevState: DiffViewState, snapshot: Any) {
        val updatedDueToDataChange = (snapshot as Boolean)
        if (updatedDueToDataChange) {
            commonFunctionalityForNewContentOrOnFirstMount()
            if (state.currentCommentBoxRowPosition != null) {
                hideCommentBox()
            }
        }
    }

    override fun componentDidMount() {
        commonFunctionalityForNewContentOrOnFirstMount()
        addGutterListeners()
        addEditorShortcutListeners()
    }

    private fun commonFunctionalityForNewContentOrOnFirstMount() {
        assignEditorVars()
        if (props.fileDiff.hasBothFiles()) {
            decorateDiffView()
        } else {
            decorateSingleFileView()
        }
        manageCommentFunctionality()
    }

    private fun assignEditorVars() {
        leftEditor = if (props.fileDiff.hasOldFile()) {
            ace.edit(LEFT_EDITOR_DIV_ID) as Editor
        } else {
            null
        }
        rightEditor = if (props.fileDiff.hasNewFile()) {
            ace.edit(RIGHT_EDITOR_DIV_ID) as Editor
        } else {
            null
        }
    }

    private fun decorateSingleFileView() {
        val (editor, lines, className) = if (props.fileDiff.hasNewFile()) {
            Triple(ace.edit(RIGHT_EDITOR_DIV_ID) as Editor, props.fileDiff.newFile!!.fileLines, ComponentStyles.getClassName { ComponentStyles::diffViewNewTextBackground })
        } else {
            Triple(ace.edit(LEFT_EDITOR_DIV_ID) as Editor, props.fileDiff.oldFile!!.fileLines, ComponentStyles.getClassName { ComponentStyles::diffViewDeletedTextGutter })
        }
        lines.forEachIndexed { index, _ ->
            editor.getSession().addGutterDecoration(index.toDouble(),  className)
        }
    }

    private fun calculateMdWidth() : Number {
        return if (props.fileDiff.hasBothFiles()) {
            6
        } else {
            12
        }
    }

    /**
     * The reason commentBox is rendered in the DiffView and not the CodeView is
     * because we want to display the comment box for a given editor over the
     * sister editor (eg: leftEditor comment box will be shown in the right
     * editor).
     */
    private fun RBuilder.renderCommentBox() {
        val screenPosition = editorForCurrentComment.convertToScreenCoordinates(state.currentCommentBoxRowPosition!!)
        styledDiv {
            css {
                position = Position.absolute
                top =  screenPosition.second.px + 8.px
                left = editorForCurrentComment.commentBoxXPosition()
            }
            commentThread {
                attrs {
                    comments = editorForCurrentComment.commentHandler.existingComments[state.currentCommentBoxRowPosition!!] ?: listOf()
                    newComments = editorForCurrentComment.commentHandler.getNewCommentAt(state.currentCommentBoxRowPosition!!)
                    onCommentAdd = handleNewComments
                    hideMe = hideCommentBox
                }
            }
        }

    }

    private fun manageCommentFunctionality() {
        leftEditorCommentsWrapper = if (props.fileDiff.hasOldFile()) {
            AceCommentsWrapper(
                editor = leftEditor!!,
                commentBoxXPosition = if (props.fileDiff.hasBothFiles()) {
                    determineCommentBoxXPositionForLeftEditor
                } else {
                    determineCommentBoxXPositionForSingleEditor
                },
                commentHandler = props.oldFileNewCommentHandler
            )
        } else {
            null
        }
        rightEditorCommentsWrapper= if (props.fileDiff.hasNewFile()) {
            AceCommentsWrapper(
                editor = rightEditor!!,
                commentBoxXPosition = if (props.fileDiff.hasBothFiles()) {
                    determineCommentBoxXPositionForRightEditor
                } else {
                    determineCommentBoxXPositionForSingleEditor
                },
                commentHandler = props.newFileNewCommentHandler
            )
        } else {
            null
        }

        /** Create maps for existing comments */
        leftEditorCommentsWrapper?.highlightOldCommentLines()
        rightEditorCommentsWrapper?.highlightOldCommentLines()

        /** Setup Right and left editor vertical scroll listeners that manage pane sync as well as hide comment on scroll */
        rightEditor?.getSession()?.on("changeScrollTop", syncLeftEditorTopScroll )
        leftEditor?.getSession()?.on("changeScrollTop", syncRightEditorTopScroll)

    }

    private val determineCommentBoxXPositionForLeftEditor: () -> LinearDimension = {
        rightEditorCommentsWrapper!!.convertToScreenCoordinates(state.currentCommentBoxRowPosition!!).first.px + 8.px
    }

    private val determineCommentBoxXPositionForRightEditor: () -> LinearDimension = {
        val screenPosition = editorForCurrentComment.convertToScreenCoordinates(state.currentCommentBoxRowPosition!!)
        screenPosition.first.px - commentBoxWidth - 65.px
    }

    private val determineCommentBoxXPositionForSingleEditor: () -> LinearDimension = {
        val screenPosition = editorForCurrentComment.convertToScreenCoordinates(state.currentCommentBoxRowPosition!!)
        screenPosition.first.px
    }

    private fun addEditorShortcutListeners() {
        UniversalKeyboardShortcutHandler.registerNumericEndKey('c', handleKeyboardTriggeredComments)
        UniversalKeyboardShortcutHandler.registerNumericEndKey('g', handleKeyboardTriggeredLineJump)
        UniversalKeyboardShortcutHandler.registerShortcut(DiffViewShortcuts.WindowOther.shortcutString, handleWindowSwitch, noOp)
        UniversalKeyboardShortcutHandler.registerShortcut(DiffViewShortcuts.PageDown.shortcutString, handlePageDown, noOp)
        UniversalKeyboardShortcutHandler.registerShortcut(DiffViewShortcuts.PageUp.shortcutString, handlePageUp, noOp)
    }

    val getActiveEditor: () -> Editor? = {
        if (leftEditor == null || leftEditor?.isFocused() == false) {
            rightEditor
        } else {
            leftEditor
        }
    }
    private val handlePageDown: () -> Unit = {
        getActiveEditor()?.gotoPageDown()
    }
    private val handlePageUp: () -> Unit = {
        getActiveEditor()?.gotoPageUp()
    }

    private val handleWindowSwitch: () -> Unit = {
        if (leftEditor == null || leftEditor?.isFocused() == true) {
            rightEditor?.focus()
        } else {
            leftEditor?.focus()
        }
    }

    private val noOp: (String) -> Unit = {

    }

    private val handleKeyboardTriggeredLineJump: (Int) -> Unit = { rowNumber ->
        jumpAndScrollTo(leftEditor, rowNumber )
        jumpAndScrollTo(rightEditor, rowNumber )
    }

    private val handleKeyboardTriggeredComments: (Int) -> Unit = { commentForRow ->
        /** First figure out the active editor */
        val activeEditor = when {
            leftEditor?.isFocused() ?: false -> {
                leftEditor!!
            }
            rightEditor?.isFocused() ?: false ->  {
                rightEditor!!
            }
            else -> {
                // Ideally should never come here - but what you gonna do! Shit happens at times. The world works in mysterious ways!
                if (rightEditor != null ) {
                    rightEditor!!
                } else {
                    leftEditor!!
                }
            }
        }
        handleCommentIntentFor(activeEditor, commentForRow - 1)
    }

    private fun addGutterListeners() {
        leftEditor?.applyGutterListener()
        rightEditor?.applyGutterListener()
    }


    private fun decorateDiffView() {
        /** Highlight relevant diff items */
        val leftEditor = ace.edit(LEFT_EDITOR_DIV_ID) as Editor
        val rightEditor = ace.edit(RIGHT_EDITOR_DIV_ID) as Editor
        TextDiffProcessor(leftEditor, rightEditor)
            .apply {
                processEditList(props.fileDiff)
                highlightLinesAddedForBalance(
                    oldFileLines = props.fileDiff.oldFile?.fileLines ?: listOf(),
                    newFileLines = props.fileDiff.newFile?.fileLines ?: listOf()
                )
            }

        jumpToFirstHunk()
    }

    private val handleNewComments: (String) -> Unit = { commentBody ->
        val createdAt = Date().toISOString()
        this.editorForCurrentComment.commentHandler.addNewComment(commentBody, state.currentCommentBoxRowPosition!!, createdAt)
        this.editorForCurrentComment.highlightNewCommentLines()
        forceUpdate()
    }

    private val syncRightEditorTopScroll: (Number) -> Unit = { scrollTopFromLeftEditor ->
        rightEditor?.getSession()?.setScrollTop(scrollTopFromLeftEditor)
        hideCommentBox()
    }

    private val syncLeftEditorTopScroll: (Number) -> Unit = { scrollTopFromRightEditor ->
        leftEditor?.getSession()?.setScrollTop(scrollTopFromRightEditor)
        hideCommentBox()
    }

    private fun leftSideTextId(): String {
        return "left-${props.fileDiff.getUniqueIdentifier()}"
    }

    private fun rightSideTextId(): String {
        return "right-${props.fileDiff.getUniqueIdentifier()}"
    }

    private fun Editor.applyGutterListener() {
        this.on("guttermousedown", gutterListenerForComments)
    }

    private val gutterListenerForComments: (MouseEvent) -> Unit = { event ->
        val documentPosition = event.getDocumentPosition()
        val commentHandled = handleCommentIntentFor(event.editor as Editor, documentPosition.row.toInt())
        if (commentHandled) {
            event.stop()
        }
    }

    private val handleCommentIntentFor: (editor: Editor, commentForRow: Int) -> Boolean = { editor, commentForRow ->
        val isLeftEditor = LEFT_EDITOR_DIV_ID == ( editor.container.id )
        editorForCurrentComment = if (isLeftEditor) {
            leftEditorCommentsWrapper!!
        } else {
            rightEditorCommentsWrapper!!
        }
        if (editorForCurrentComment.isCommentAllowed()) {
            showCommentBox(commentForRow)
            true
        } else {
            /** Basically toggle */
            hideCommentBox()
            false
        }
    }

    private val showCommentBox: (Int) -> Unit = { rowNumber->
        setState {
            currentCommentBoxRowPosition = rowNumber
        }
    }

    private val hideCommentBox: () -> Unit = {
        setState {
            currentCommentBoxRowPosition = null
        }
    }

    private val jumpToNextHunkFromCurrentState: () -> Unit = {
        jumpToNextHunk(state.currentHunkIndex)
    }

    private val jumpToPreviousHunkFromCurrentState: () -> Unit = {
        jumpToNextHunk(max(state.currentHunkIndex - 2, -1))
    }

    /**
     * This exists separately specifically to be called  whenever we have new text in _both_ the editors
     * The reason for that is that we cannot use [state.currentHunkIndex] whenever we get _new text_ in the editors
     * Since whenever we get new text, the value stored in the state still corresponds to the value we had reached to in previous diff
     * The call to this method (which calls [jumpToNextHunk] with a value of -1 actually brings the cursor to the first hunk && sets the state
     * variable value to 0
     */
    private val jumpToFirstHunk: () -> Unit = {
        jumpToNextHunk(-1)
    }

    private val jumpToNextHunk: (Int) -> Unit = { givenHunkIndex ->
        val (nextEditIndex, positionToJumpTo) = props.fileDiff.nextEditPosition(givenHunkIndex)
        if (nextEditIndex == null) {
            console.log("Seems we have reached the last hunk, jumping to next file")
            props.defaultActionBarActions.find { it.assignedShortcut == DiffViewShortcuts.NextFile.shortcutString } ?.handler ?.invoke()
        } else {
            jumpAndScrollTo(leftEditor!!, positionToJumpTo!! + 1)
            jumpAndScrollTo(rightEditor!!, positionToJumpTo + 1)
            setState {
                currentHunkIndex = nextEditIndex
            }
        }
    }

    private val jumpAndScrollTo: (Editor?, Int) -> Unit = { editor, rowNumber ->
        editor?.gotoLine(lineNumber = rowNumber, column = 0, animate = true)
        editor?.scrollToLine(lineNumber = rowNumber, animate = false, center = true)
    }

    private val additionalShortcutsForTwoPaneView = listOf(
        ActionBarShortcut(NextHunk.label, NextHunk.shortcutString, jumpToNextHunkFromCurrentState),
        ActionBarShortcut(PreviousHunk.label, PreviousHunk.shortcutString, jumpToPreviousHunkFromCurrentState)
    )

}

fun RBuilder.diffView(handler: DiffViewProps.() -> Unit): ReactElement {
    return child(DiffView::class) {
        this.attrs(handler)
    }
}

