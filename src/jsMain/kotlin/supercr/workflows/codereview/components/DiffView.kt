package supercr.workflows.codereview.components

import Grid
import Paper
import codereview.Edit
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.getClassName
import supercr.css.ComponentStyles
import supercr.workflows.codereview.processor.TextDiffProcessor

external interface DiffViewProps: RProps {
    var branchName: String
    var oldText: String
    var newText: String
    var editList: List<Edit>
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
                codeText = props.oldText
                xsValueToUse = 5
            }
            codeView {
                id = rightEditorId()
                codeText = props.newText
                xsValueToUse = 7
            }
        }
    }

    override fun componentDidMount() {
        leftEditor = ace.edit(leftEditorId())
        rightEditor = ace.edit(rightEditorId())
        /** Highlight relevant diff items */
        TextDiffProcessor(leftEditor, rightEditor).processEditList(props.editList)
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
        return "left-${props.identifier}"
    }

    private fun rightEditorId(): String {
        return "right-${props.identifier}"
    }
}

fun RBuilder.diffView(handler: DiffViewProps.() -> Unit): ReactElement {
    return child(DiffView::class) {
        this.attrs(handler)
    }
}

