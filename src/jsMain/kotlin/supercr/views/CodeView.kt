package supercr.views

import AceEditor
import react.RBuilder
import react.RClass
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement

external interface CodeViewProps: RProps {
    var codeText: String
    var id: String
    /** If present, will apply the clazzName to the editor */
    var className: String
}

/**
 * A simple wrapper over [AceEditor] with sensible defaults
 */
class CodeView : RComponent<CodeViewProps, RState>() {
    override fun RBuilder.render() {
        AceEditor {
            attrs {
                mode = "java"
                theme = "github"
                name = props.id
                readOnly = true
                value = props.codeText
                width = "inherit"
                highlightActiveLine = false
                className = props.className
            }
        }
    }
}

fun RBuilder.codeView(handler: CodeViewProps.() -> Unit): ReactElement {
    return child(CodeView::class) {
        this.attrs(handler)
    }
}
