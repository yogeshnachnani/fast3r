@file:JsModule("react-ace")
@file:JsNonModule

import kotlinext.js.Object
import react.RClass
import react.RProps

@JsName("default")
external val AceEditor: RClass<AceEditorProps>

external interface AceEditorProps: RProps {
    var mode: String
    var theme: String
    /** Unique id of the div */
    var name: String
    var readOnly: Boolean
    var editorProps: Object
    var placeHolder: String
    var value: String
    var width: String
    var height: String
    var highlightActiveLine: Boolean
    var className: String
}
