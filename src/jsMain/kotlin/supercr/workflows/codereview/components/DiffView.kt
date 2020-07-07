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
import supercr.css.GutterDecorationStyles
import supercr.workflows.codereview.processor.TextDiffProcessor
import supercr.css.commentBoxWidth
import supercr.workflows.codereview.processor.FileCommentHandler
import supercr.workflows.codereview.processor.hasBothFiles
import supercr.workflows.codereview.processor.nextEditPosition

external interface DiffViewProps: RProps {
    var fileDiff: FileDiffV2
    var identifier: String
    var oldFileNewCommentHandler: FileCommentHandler
    var newFileNewCommentHandler: FileCommentHandler
    var addMoreActionsToActionBar: (List<ActionBarShortcut>) -> Unit
}
external interface DiffViewState: RState {
    var currentCommentBoxPosition: RowColObject?
    /** The index of the current Hunk (or Edit) in fileDiff.editList */
    var currentHunkIndex: Int
}

class AceWrapper(
    var editor: Editor,
    var oldComments: Map<Int, List<FileLineItem.Comment>>,
    val commentBoxXPosition: (RowColObject) -> LinearDimension,
    val getNewComments: (Int) -> List<FileLineItem.Comment>,
    val commentHandler: FileCommentHandler
) {
    fun highlightCommentLines() {
        oldComments.keys.highlightCommentLines(editor)
    }

    private fun Set<Int>.highlightCommentLines(forEditor: Editor) {
        this
            .map { viewPosition ->
                forEditor.getSession().addGutterDecoration(viewPosition, GutterDecorationStyles.commentIcon)
            }
    }

    fun scrollToLine(lineNumber: Int, center: Boolean, animate: Boolean) {
        editor.scrollToLine(lineNumber.toDouble(), center, animate)
    }
}

const val LEFT_EDITOR_DIV_ID = "fast3r-left-view"
const val RIGHT_EDITOR_DIV_ID = "fast3r-right-view"

class DiffView: RComponent<DiffViewProps, DiffViewState>() {
    val ace = js("require('ace-builds/src-noconflict/ace')")
    private var leftEditorWrapper: AceWrapper? = null
    private var rightEditorWrapper: AceWrapper? = null
    private lateinit var editorForCurrentComment: AceWrapper

    override fun DiffViewState.init() {
        currentCommentBoxPosition = null
        currentHunkIndex = -1
    }

    override fun RBuilder.render() {
        Grid {
            attrs {
                item = false
                container = true
                alignItems = "center"
                direction = "row"
                justify = "center"
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
                        }
                    }
                }
            }
            if (state.currentCommentBoxPosition != null) {
                renderCommentBox()
            }
        }
    }

    override fun shouldComponentUpdate(nextProps: DiffViewProps, nextState: DiffViewState): Boolean {
        console.log("Asking the all important question of moving from ${props.identifier} to ${nextProps.identifier}")
        return (nextProps.identifier != props.identifier) || (nextState.currentCommentBoxPosition != state.currentCommentBoxPosition)
    }

    override fun componentDidUpdate(prevProps: DiffViewProps, prevState: DiffViewState, snapshot: Any) {
        if (props.fileDiff.hasBothFiles()) {
            onMountOrUpdate()
        }
    }

    override fun componentDidMount() {
        if (props.fileDiff.hasBothFiles()) {
            onMountOrUpdate()
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
        val screenPosition = state.currentCommentBoxPosition!!.convertToScreenCoordinates(editorForCurrentComment.editor)
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
                }
            }
        }

    }

    private fun onMountOrUpdate() {
        val leftEditor = ace.edit(LEFT_EDITOR_DIV_ID) as Editor
        val rightEditor = ace.edit(RIGHT_EDITOR_DIV_ID) as Editor
        leftEditorWrapper = AceWrapper(
            editor = leftEditor,
            oldComments = props.fileDiff.oldFile?.retrieveMapOfViewPositionToComments() ?: emptyMap(),
            commentBoxXPosition = { rowColObject ->
                state.currentCommentBoxPosition!!.convertToScreenCoordinates(forEditor = rightEditor).first.px - 45.px
            },
            getNewComments = { viewPosition ->
                props.oldFileNewCommentHandler.comments[viewPosition] ?: listOf()
            },
            commentHandler = props.oldFileNewCommentHandler
        )
        rightEditorWrapper = AceWrapper(
            editor = rightEditor,
            oldComments = props.fileDiff.newFile?.retrieveMapOfViewPositionToComments() ?: emptyMap(),
            commentBoxXPosition = { rowColObject ->
                val screenPosition = state.currentCommentBoxPosition!!.convertToScreenCoordinates(editorForCurrentComment!!.editor)
                screenPosition.first.px - commentBoxWidth - 45.px
            },
            getNewComments = { viewPosition ->
                props.newFileNewCommentHandler.comments[viewPosition] ?: listOf()
            },
            commentHandler = props.newFileNewCommentHandler
        )

        /** Highlight relevant diff items */
        TextDiffProcessor(leftEditor, rightEditor)
            .apply {
                processEditList(props.fileDiff.editList)
                highlightLinesAddedForBalance(props.fileDiff.oldFile?.fileLines ?: listOf(), props.fileDiff.newFile?.fileLines ?: listOf())
            }
        /** Setup Right and left editor vertial scroll sync */
        rightEditor.getSession().on("changeScrollTop", syncLeftEditorTopScroll )
        leftEditor.getSession().on("changeScrollTop", syncRightEditorTopScroll)

        /** Create maps for comments */
        leftEditorWrapper?.highlightCommentLines()
        rightEditorWrapper?.highlightCommentLines()

        /** Gutter Listeners */
        applyGutterListener(leftEditor)
        applyGutterListener(rightEditor)

        addActionCommandsIfApplicable()
    }

    private fun addActionCommandsIfApplicable() {
        if (props.fileDiff.hasBothFiles()) {
            props.addMoreActionsToActionBar(
                listOf(
                    ActionBarShortcut("Next Hunk", "sl", jumpToNextHunk),
                    ActionBarShortcut("Prev Hunk", "sh", {console.log("Will move hunk backword")})
                )
            )
        }
    }

    private val handleNewComments: (String) -> Unit = { commentBody ->
        this.editorForCurrentComment!!.commentHandler.addNewComment(commentBody, state.currentCommentBoxPosition!!.row.toInt())
        forceUpdate()
    }

    private val syncRightEditorTopScroll: (Number) -> Unit = { scrollTopFromLeftEditor ->
        rightEditorWrapper?.editor?.getSession()?.setScrollTop(scrollTopFromLeftEditor)
        hideCommentBox()
    }

    private val syncLeftEditorTopScroll: (Number) -> Unit = { scrollTopFromRightEditor ->
        leftEditorWrapper?.editor?.getSession()?.setScrollTop(scrollTopFromRightEditor)
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

    private fun RowColObject.convertToScreenCoordinates(forEditor: Editor): Pair<Number, Number> {
        val screenPosition = forEditor.renderer.textToScreenCoordinates(row, column)
        return Pair(screenPosition.pageX, screenPosition.pageY)
    }

    private fun applyGutterListener(forEditor: Editor) {
        forEditor.on("guttermousedown", internalGutterListener)
    }

    private val internalGutterListener: (MouseEvent) -> Unit = { event ->
        val documentPosition = event.getDocumentPosition()
        val isLeftEditor = LEFT_EDITOR_DIV_ID == ( event.editor.container.id as String )
        console.log("Got an event on : ", event.domEvent, "with position ", documentPosition , " with isLeftEditor = ", isLeftEditor)
        editorForCurrentComment = if (isLeftEditor) {
            leftEditorWrapper!!
        } else {
            rightEditorWrapper!!
        }
        if (editorForCurrentComment.oldComments.containsKey(documentPosition.row)) {
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

    private val jumpToNextHunk: () -> Unit = {
        val (nextEditIndex, positionToJumpTo) = props.fileDiff.nextEditPosition(state.currentHunkIndex)
        console.log("Seems like we'll be jumping to $positionToJumpTo . and next edit index is $nextEditIndex")
        if (nextEditIndex == null) {
            console.log("Seems we have reached the end")
        } else {
            leftEditorWrapper!!.scrollToLine(lineNumber = positionToJumpTo!! + 1, center = true, animate = true)
            rightEditorWrapper!!.scrollToLine(lineNumber = positionToJumpTo + 1, center = true, animate = true)
            setState {
                currentHunkIndex = nextEditIndex
            }
        }
    }
}

fun RBuilder.diffView(handler: DiffViewProps.() -> Unit): ReactElement {
    return child(DiffView::class) {
        this.attrs(handler)
    }
}

