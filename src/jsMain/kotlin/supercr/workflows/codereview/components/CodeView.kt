package supercr.workflows.codereview.components

import AceEditor
import Grid
import MouseEvent
import RowColObject
import codereview.FileData
import codereview.FileLineItem
import codereview.getText
import codereview.retrieveAllLineItems
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import supercr.css.ComponentStyles
import supercr.css.GutterDecorationStyles

external interface CodeViewProps: RProps {
    var fileData: FileData
    var id: String
    /** If present, will apply the clazzName to the editor */
    var className: String
    var xsValueToUse: Number
}

external interface CodeViewState: RState {
}


/**
 * A simple wrapper over [AceEditor] with sensible defaults
 */
class CodeView(
    constructorProps: CodeViewProps
): RComponent<CodeViewProps, CodeViewState>(constructorProps) {
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
                    theme = "clouds_midnight"
                    name = props.id
                    readOnly = true
                    value = props.fileData.getText()
                    width = "inherit"
                    highlightActiveLine = true
                    height = "800px"
                    className = "${props.className} ${ ComponentStyles.getClassName { ComponentStyles::codeViewEditor } }"
                    wrapEnabled = false
                }
            }
        }
    }
}


fun RBuilder.codeView(handler: RElementBuilder<CodeViewProps>.() -> Unit): ReactElement {
    return child(CodeView::class) {
        handler()
    }
}
