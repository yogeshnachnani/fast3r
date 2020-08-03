package supercr.workflows.codereview.components

import Editor
import Grid
import MouseEvent
import RowColObject
import codereview.FileData
import codereview.FileDiffV2
import codereview.FileLineItem
import codereview.getText
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
import supercr.kb.UniversalShortcuts
import supercr.kb.getShortcutString
import supercr.workflows.codereview.processor.FileCommentHandler
import supercr.workflows.codereview.processor.hasBothFiles
import supercr.workflows.codereview.processor.nextEditPosition

external interface DiffViewProps: RProps {
    var fileDiff: FileDiffV2
    var identifier: String
    var oldFileNewCommentHandler: FileCommentHandler
    var newFileNewCommentHandler: FileCommentHandler
    var defaultActionBarActions: List<ActionBarShortcut>
}
external interface DiffViewState: RState {
    var currentCommentBoxPosition: RowColObject?
    /** The index of the current Hunk (or Edit) in fileDiff.editList */
    var currentHunkIndex: Int
}

class AceCommentsWrapper(
    private var editor: Editor,
    var oldComments: Map<Int, List<FileLineItem.Comment>>,
    val commentBoxXPosition: (RowColObject) -> LinearDimension,
    val getNewComments: (Int) -> List<FileLineItem.Comment>,
    val commentHandler: FileCommentHandler
) {
    fun highlightCommentLines() {
        oldComments.keys.highlightCommentLines(editor)
    }

    fun isCommentAllowed(): Boolean {
        /** TODO: implement. One way to check for existing comments is : oldComments.containsKey(documentPosition.row) */
        return true
    }

    fun convertToScreenCoordinates(rowColObject: RowColObject): Pair<Number, Number> {
        return with(rowColObject) {
            val screenPosition = editor.renderer.textToScreenCoordinates(row, column)
            Pair(screenPosition.pageX, screenPosition.pageY)
        }
    }

    private fun Set<Int>.highlightCommentLines(forEditor: Editor) {
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
        currentCommentBoxPosition = null
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
                            fileText = props.fileDiff.oldFile?.getText() ?: ""
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
                            fileText = props.fileDiff.newFile?.getText() ?: ""
                            divId = RIGHT_EDITOR_DIV_ID
                            actionBarActions = props.defaultActionBarActions.plus(additionalShortcutsForTwoPaneView)
                        }
                    }
                }
            }
            if (state.currentCommentBoxPosition != null) {
                renderCommentBox()
            }
        }
    }

    override fun getSnapshotBeforeUpdate(prevProps: DiffViewProps, prevState: DiffViewState): Any {
        return prevProps.identifier != props.identifier
    }

    override fun shouldComponentUpdate(nextProps: DiffViewProps, nextState: DiffViewState): Boolean {
        return (nextProps.identifier != props.identifier) || (nextState.currentCommentBoxPosition != state.currentCommentBoxPosition)
    }

    override fun componentDidUpdate(prevProps: DiffViewProps, prevState: DiffViewState, snapshot: Any) {
        val updatedDueToDataChange = (snapshot as Boolean)
        if (updatedDueToDataChange) {
            updateOrFirstMountCommon()
            if (state.currentCommentBoxPosition != null) {
                hideCommentBox()
            }
        }
    }

    override fun componentDidMount() {
        updateOrFirstMountCommon()
        addGutterListeners()
    }

    private fun updateOrFirstMountCommon() {
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
        val screenPosition = editorForCurrentComment.convertToScreenCoordinates(state.currentCommentBoxPosition!!)
        styledDiv {
            css {
                position = Position.absolute
                top =  screenPosition.second.px + 8.px
                left = editorForCurrentComment.commentBoxXPosition(state.currentCommentBoxPosition!!)
            }
            commentThread {
                attrs {
                    comments = editorForCurrentComment.oldComments[state.currentCommentBoxPosition!!.row] ?: listOf()
                    newComments = editorForCurrentComment.getNewComments(state.currentCommentBoxPosition!!.row.toInt())
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
                oldComments = props.fileDiff.oldFile?.retrieveMapOfViewPositionToComments() ?: emptyMap(),
                commentBoxXPosition = if (props.fileDiff.hasBothFiles()) {
                    determineCommentBoxXPositionForLeftEditor
                } else {
                    determineCommentBoxXPositionForSingleEditor
                },
                getNewComments = { viewPosition ->
                    props.oldFileNewCommentHandler.comments[viewPosition] ?: listOf()
                },
                commentHandler = props.oldFileNewCommentHandler
            )
        } else {
            null
        }
        rightEditorCommentsWrapper= if (props.fileDiff.hasNewFile()) {
            AceCommentsWrapper(
                editor = rightEditor!!,
                oldComments = props.fileDiff.newFile?.retrieveMapOfViewPositionToComments() ?: emptyMap(),
                commentBoxXPosition = if (props.fileDiff.hasBothFiles()) {
                    determineCommentBoxXPositionForRightEditor
                } else {
                    determineCommentBoxXPositionForSingleEditor
                },
                getNewComments = { viewPosition ->
                    props.newFileNewCommentHandler.comments[viewPosition] ?: listOf()
                },
                commentHandler = props.newFileNewCommentHandler
            )
        } else {
            null
        }

        /** Create maps for existing comments */
        leftEditorCommentsWrapper?.highlightCommentLines()
        rightEditorCommentsWrapper?.highlightCommentLines()

        /** Setup Right and left editor vertical scroll listeners that manage pane sync as well as hide comment on scroll */
        rightEditor?.getSession()?.on("changeScrollTop", syncLeftEditorTopScroll )
        leftEditor?.getSession()?.on("changeScrollTop", syncRightEditorTopScroll)

    }

    private val determineCommentBoxXPositionForLeftEditor: (RowColObject) -> LinearDimension = { rowColObject ->
        rightEditorCommentsWrapper!!.convertToScreenCoordinates(state.currentCommentBoxPosition!!).first.px + 8.px
    }

    private val determineCommentBoxXPositionForRightEditor: (RowColObject) -> LinearDimension = {
        val screenPosition = editorForCurrentComment.convertToScreenCoordinates(state.currentCommentBoxPosition!!)
        screenPosition.first.px - commentBoxWidth - 65.px
    }

    private val determineCommentBoxXPositionForSingleEditor: (RowColObject) -> LinearDimension = {
        val screenPosition = editorForCurrentComment.convertToScreenCoordinates(state.currentCommentBoxPosition!!)
        screenPosition.first.px
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
                processEditList(props.fileDiff.editList)
                highlightLinesAddedForBalance(
                    oldFileLines = props.fileDiff.oldFile?.fileLines ?: listOf(),
                    newFileLines = props.fileDiff.newFile?.fileLines ?: listOf()
                )
            }

        jumpToFirstHunk()
    }

    private val handleNewComments: (String) -> Unit = { commentBody ->
        this.editorForCurrentComment.commentHandler.addNewComment(commentBody, state.currentCommentBoxPosition!!.row.toInt())
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

    private fun FileData.retrieveMapOfViewPositionToComments(): Map<Int, List<FileLineItem.Comment>> {
        val sampleComments = mutableListOf(
            FileLineItem.Comment("This is a single line comment", "2020-06-26T09:44:44.018189Z", "2020-06-26T09:44:44.018189Z", "yogeshnachnani"),
            FileLineItem.Comment("This is a much longer comment so it should ideally span more lines", "2020-06-26T09:44:44.018189Z", "2020-06-26T09:44:44.018189Z", "yogeshnachnani")
        )
        return sampleComments. mapIndexed { index, comment ->
            Pair(index, listOf(comment, comment))
        }
            .associate { it }
//        return this.retrieveAllLineItems()
//            .mapNotNull { (viewPosition, filePosition, lineItems) ->
//                val comments = lineItems.filterIsInstance<FileLineItem.Comment>()
//                if (comments.isNotEmpty()) {
//                    Pair(viewPosition, FilePositionAndComments(filePosition, comments))
//                } else {
//                    null
//                }
//            }
//            .associate { it }
    }

    private fun Editor.applyGutterListener() {
        this.on("guttermousedown", gutterListenerForComments)
    }

    private val gutterListenerForComments: (MouseEvent) -> Unit = { event ->
        val documentPosition = event.getDocumentPosition()
        val isLeftEditor = LEFT_EDITOR_DIV_ID == ( event.editor.container.id as String )
        editorForCurrentComment = if (isLeftEditor) {
            leftEditorCommentsWrapper!!
        } else {
            rightEditorCommentsWrapper!!
        }
        if (editorForCurrentComment.isCommentAllowed()) {
            event.stop()
            showCommentBox(documentPosition)
        } else {
            /** Basically toggle */
            hideCommentBox()
        }
    }

    private val showCommentBox: (RowColObject) -> Unit = { documentPosition->
        setState {
            currentCommentBoxPosition = documentPosition
        }
    }

    private val hideCommentBox: () -> Unit = {
        setState {
            currentCommentBoxPosition = null
        }
    }

    private val jumpToNextHunkFromCurrentState: () -> Unit = {
        jumpToNextHunk(state.currentHunkIndex)
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
        console.log("Seems like we'll be jumping to $positionToJumpTo . and next edit index is $nextEditIndex")
        if (nextEditIndex == null) {
            console.log("Seems we have reached the last hunk, jumping to next file")
            props.defaultActionBarActions.find { it.assignedShortcut == UniversalShortcuts.NextFile.getShortcutString() } ?.handler ?.invoke()
        } else {
            leftEditor!!.gotoLine(lineNumber = positionToJumpTo!! + 1, column = 0, animate = true)
            rightEditor!!.gotoLine(lineNumber = positionToJumpTo + 1, column = 0, animate = true)
            setState {
                currentHunkIndex = nextEditIndex
            }
        }
    }
    private val additionalShortcutsForTwoPaneView = listOf(
        ActionBarShortcut("Next Hunk", "sl", jumpToNextHunkFromCurrentState),
        ActionBarShortcut("Prev Hunk", "sh", {console.log("Will move hunk backword")})
    )

}

fun RBuilder.diffView(handler: DiffViewProps.() -> Unit): ReactElement {
    return child(DiffView::class) {
        this.attrs(handler)
    }
}

