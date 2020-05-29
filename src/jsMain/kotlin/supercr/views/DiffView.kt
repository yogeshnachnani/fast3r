package supercr.views

import AceEditor
import Grid
import codereview.DiffEditType
import codereview.Edit
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import supercr.css.TextStyles
import supercr.processor.TextDiffProcessor

external interface DiffViewProps: RProps {
    var branchName: String
    var oldText: String
    var newText: String
    var editList: List<Edit>
}
external interface DiffViewState: RState {
    var currentFileName: String
}

class DiffView: RComponent<DiffViewProps, DiffViewState>() {
    val ace = js("require('ace-builds/src-noconflict/ace')")
    val webpackResolver = js("require('ace-builds/webpack-resolver')")
    val theme = js("require('ace-builds/src-noconflict/theme-solarized_light')")
    val split = js("require('ace-builds/src-noconflict/ext-split')")
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
                spacing = 2
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    xs = 6
                }
                AceEditor {
                    attrs {
                        mode = "java"
                        theme = "github"
                        name = "left-main-editor"
                        readOnly = true
                        value = props.oldText
                        width = "inherit"
                    }
                }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    xs = 6
                }
                AceEditor {
                    attrs {
                        mode = "java"
                        theme = "github"
                        name = "right-main-editor"
                        readOnly = true
                        value = props.newText
                        width = "inherit"
                    }
                }
            }
        }
    }

    override fun componentDidMount() {
        leftEditor = ace.edit("left-main-editor")
        rightEditor = ace.edit("right-main-editor")
        TextDiffProcessor(leftEditor, rightEditor).processEditList(props.editList)
//        Split(document.getElementById("main-editor")!!, "ace/theme/solarized_light", 2)
    }
}

fun RBuilder.diffView(handler: DiffViewProps.() -> Unit): ReactElement {
    return child(DiffView::class) {
        this.attrs(handler)
    }
}

