package supercr.workflows.codereview.components

import AceEditor
import Grid
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.getClassName
import supercr.css.ComponentStyles

external interface CodeViewProps: RProps {
    var codeText: String
    var id: String
    /** If present, will apply the clazzName to the editor */
    var className: String
    var xsValueToUse: Number
}

/**
 * A simple wrapper over [AceEditor] with sensible defaults
 */
class CodeView : RComponent<CodeViewProps, RState>() {
    /** These are required to load the editor properly */
    val ace = js("require('ace-builds/src-noconflict/ace')")
    val webpackResolver = js("require('ace-builds/webpack-resolver')")
    val theme = js("require('ace-builds/src-noconflict/theme-solarized_light')")
    val split = js("require('ace-builds/src-noconflict/ext-split')")
    override fun RBuilder.render() {
        Grid {
            attrs {
                item = true
                container = false
                md = props.xsValueToUse
            }
            AceEditor {
                attrs {
                    mode = "java"
                    theme = "github"
                    name = props.id
                    readOnly = true
                    value = props.codeText
                    width = "inherit"
                    highlightActiveLine = false
                    className = "${props.className} ${ ComponentStyles.getClassName { ComponentStyles::codeViewEditor } }"
                }
            }
        }
    }
}

fun RBuilder.codeView(handler: CodeViewProps.() -> Unit): ReactElement {
    return child(CodeView::class) {
        this.attrs(handler)
    }
}
