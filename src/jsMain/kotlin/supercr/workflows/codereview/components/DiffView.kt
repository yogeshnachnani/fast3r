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
                xsValueToUse = 6
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 1
                }
                Paper {
                    attrs {
                        variant = "outlined"
                        className = ComponentStyles.getClassName { ComponentStyles::infoPaper }
                    }
                    + "For Comments"
                }
            }
        }
    }

    override fun componentDidMount() {
        leftEditor = ace.edit(leftEditorId())
        rightEditor = ace.edit(rightEditorId())
        TextDiffProcessor(leftEditor, rightEditor).processEditList(props.editList)
//        Split(document.getElementById("main-editor")!!, "ace/theme/solarized_light", 2)
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

