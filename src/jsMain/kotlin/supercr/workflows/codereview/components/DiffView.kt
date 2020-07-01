package supercr.workflows.codereview.components

import Grid
import MouseEvent
import RowColObject
import codereview.FileData
import codereview.FileDiffV2
import codereview.FileLineItem
import codereview.getUniqueIdentifier
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
import supercr.workflows.codereview.screens.FileCommentHandler

external interface DiffViewProps: RProps {
    var fileDiff: FileDiffV2
    var identifier: String
    var oldFileNewCommentHandler: FileCommentHandler
    var newFileNewCommentHandler: FileCommentHandler
}
external interface DiffViewState: RState {
    var currentCommentBoxPosition: RowColObject?
}

class AceWrapper(
    var editor: dynamic,
    var oldComments: Map<Int, List<FileLineItem.Comment>>,
    val commentBoxXPosition: (RowColObject) -> LinearDimension,
    val getNewComments: (Int) -> List<FileLineItem.Comment>,
    val commentHandler: FileCommentHandler
) {
    fun highlightCommentLines() {
        oldComments.keys.highlightCommentLines(editor)
    }

    private fun Set<Int>.highlightCommentLines(forEditor: dynamic) {
        this
            .map { viewPosition ->
                forEditor.getSession().addGutterDecoration(viewPosition, GutterDecorationStyles.commentIcon)
            }
    }
}

class DiffView: RComponent<DiffViewProps, DiffViewState>() {
    val ace = js("require('ace-builds/src-noconflict/ace')")
    private var leftEditorWrapper: AceWrapper? = null
    private var rightEditorWrapper: AceWrapper? = null
    private var editorForCurrentComment: AceWrapper? = null
    private var leftEditor: dynamic = null
    private var rightEditor: dynamic = null

    override fun DiffViewState.init() {
        currentCommentBoxPosition = null
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
            codeView {
                attrs {
                    id = leftEditorId()
                    fileData = props.fileDiff.oldFile!!
                    xsValueToUse = 6
                }
            }
            codeView {
                attrs {
                    id = rightEditorId()
                    fileData = props.fileDiff.newFile!!
                    xsValueToUse = 6
                }
            }
            if (state.currentCommentBoxPosition != null) {
                renderCommentBox()
            }
        }
    }

    override fun componentDidUpdate(prevProps: DiffViewProps, prevState: DiffViewState, snapshot: Any) {
        onMountOrUpdate()
    }

    override fun componentDidMount() {
        onMountOrUpdate()
    }

    /**
     * The reason commentBox is rendered in the DiffView and not the CodeView is
     * because we want to display the comment box for a given editor over the
     * sister editor (eg: leftEditor comment box will be shown in the right
     * editor).
     */
    private fun RBuilder.renderCommentBox() {
        val screenPosition = state.currentCommentBoxPosition!!.convertToScreenCoordinates(editorForCurrentComment!!.editor)
        styledDiv {
            css {
                position = Position.absolute
                top =  screenPosition.second.px + 8.px
                left = editorForCurrentComment!!.commentBoxXPosition(state.currentCommentBoxPosition!!)
            }
            commentThread {
                attrs {
                    comments = editorForCurrentComment!!.oldComments[state.currentCommentBoxPosition!!.row] ?: listOf()
                    newComments = editorForCurrentComment!!.getNewComments(state.currentCommentBoxPosition!!.row.toInt())
                    onCommentAdd = handleNewComments
                }
            }
        }

    }

    private fun onMountOrUpdate() {
        leftEditor = ace.edit(leftEditorId())
        rightEditor = ace.edit(rightEditorId())
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
        /** Hide Scrollbars. TODO: Find a less hacky way to do this */
        leftEditor.renderer.scrollBarV.element.style["overflowY"] = "hidden"
        rightEditor.renderer.scrollBarV.element.style["overflowY"] = "hidden"
        /** Setup Right and left editor vertial scroll sync */
        rightEditor.getSession().on("changeScrollTop", syncLeftEditorTopScroll)
        leftEditor.getSession().on("changeScrollTop", syncRightEditorTopScroll)

        /** Create maps for comments */
        leftEditorWrapper?.highlightCommentLines()
        rightEditorWrapper?.highlightCommentLines()

        /** Gutter Listeners */
        applyGutterListener(leftEditor)
        applyGutterListener(rightEditor)
    }

    private val handleNewComments: (String) -> Unit = { commentBody ->
        this.editorForCurrentComment!!.commentHandler.addNewComment(commentBody, state.currentCommentBoxPosition!!.row.toInt())
        forceUpdate()
    }

    private val syncRightEditorTopScroll: (Number) -> Unit = { scrollTopFromLeftEditor ->
        rightEditor.getSession().setScrollTop(scrollTopFromLeftEditor)
        hideCommentBox()
    }

    private val syncLeftEditorTopScroll: (Number) -> Unit = { scrollTopFromRightEditor ->
        leftEditor.getSession().setScrollTop(scrollTopFromRightEditor)
        hideCommentBox()
    }

    private fun leftEditorId(): String {
        return "left-${props.fileDiff.getUniqueIdentifier()}"
    }

    private fun rightEditorId(): String {
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

    private fun RowColObject.convertToScreenCoordinates(forEditor: dynamic): Pair<Number, Number> {
        val screenPosition = forEditor.renderer.textToScreenCoordinates(row, column)
        return Pair(screenPosition.pageX, screenPosition.pageY)
    }

    private fun applyGutterListener(forEditor: dynamic) {
        forEditor.on("guttermousedown", internalGutterListener)
    }

    private val internalGutterListener: (MouseEvent) -> Unit = { event ->
        val documentPosition = event.getDocumentPosition()
        val isLeftEditor = leftEditorId() == ( event.editor.container.id as String )
        console.log("Got an event on : ", event.domEvent, "with position ", documentPosition , " with isLeftEditor = ", isLeftEditor)
        editorForCurrentComment = if (isLeftEditor) {
            leftEditorWrapper
        } else {
            rightEditorWrapper
        }
        if (editorForCurrentComment!!.oldComments.containsKey(documentPosition.row)) {
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

}

fun RBuilder.diffView(handler: DiffViewProps.() -> Unit): ReactElement {
    return child(DiffView::class) {
        this.attrs(handler)
    }
}

