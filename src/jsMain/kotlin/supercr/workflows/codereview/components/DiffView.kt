package supercr.workflows.codereview.components

import Grid
import codereview.FileDiffV2
import codereview.getNewText
import codereview.getOldText
import codereview.getUniqueIdentifier
import kotlinx.css.pre
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import supercr.workflows.codereview.processor.TextDiffProcessor

external interface DiffViewProps: RProps {
    var fileDiff: FileDiffV2
    var identifier: String
}
external interface DiffViewState: RState {
    var currentFileName: String
}

class DiffView: RComponent<DiffViewProps, DiffViewState>() {
    val ace = js("require('ace-builds/src-noconflict/ace')")
    private var leftEditor: dynamic = null
    private var rightEditor: dynamic = null

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
                id = leftEditorId()
                codeText = props.fileDiff.getOldText()
                xsValueToUse = 6
            }
            codeView {
                id = rightEditorId()
                codeText = props.fileDiff.getNewText()
                xsValueToUse = 6
            }
        }
    }

    override fun componentDidUpdate(prevProps: DiffViewProps, prevState: DiffViewState, snapshot: Any) {
        onMountOrUpdate()
    }

    override fun componentDidMount() {
        onMountOrUpdate()
    }

    private fun onMountOrUpdate() {
        leftEditor = ace.edit(leftEditorId())
        rightEditor = ace.edit(rightEditorId())
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
    }

    private val syncRightEditorTopScroll: (Number) -> Unit = { scrollTopFromLeftEditor ->
        rightEditor.getSession().setScrollTop(scrollTopFromLeftEditor)
    }

    private val syncLeftEditorTopScroll: (Number) -> Unit = { scrollTopFromRightEditor ->
        leftEditor.getSession().setScrollTop(scrollTopFromRightEditor)
    }

    private fun leftEditorId(): String {
        return "left-${props.fileDiff.getUniqueIdentifier()}"
    }

    private fun rightEditorId(): String {
        return "right-${props.fileDiff.getUniqueIdentifier()}"
    }
}

fun RBuilder.diffView(handler: DiffViewProps.() -> Unit): ReactElement {
    return child(DiffView::class) {
        this.attrs(handler)
    }
}

