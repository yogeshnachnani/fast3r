package supercr.kb.components

import OutlinedInput
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.FocusEvent
import react.RBuilder
import react.RComponent
import react.RReadableRef
import react.RState
import react.ReactElement
import react.createRef
import react.dom.WithClassName
import supercr.kb.UniversalKeyboardShortcutHandler

external interface CtrlEnterControlledInputProps : WithClassName {
    var rows: Number
    var rowsMax : Number
    var placeholder: String
    var onInputCtrlEnter: (String) -> Unit
    var onEscape: ( () -> Unit  )?
}

external interface CtrlEnterControlledInputState : RState {

}

class CtrlEnterControlledInput : RComponent<CtrlEnterControlledInputProps, CtrlEnterControlledInputState>() {
    private lateinit var textAreaRef: RReadableRef<HTMLTextAreaElement>

    override fun CtrlEnterControlledInputState.init() {
        textAreaRef = createRef()
    }

    override fun RBuilder.render() {
        OutlinedInput {
            attrs {
                inputRef = textAreaRef
                autoFocus = true
                multiline = true
                rows = props.rows
                rowsMax = props.rowsMax
                placeholder = props.placeholder
                className = props.className
            }
        }
    }

    override fun componentDidMount() {
        textAreaRef.current?.onfocus = disableKeyboardHandlerOnFocus
        textAreaRef.current?.onblur = enableKeyboardHandlerOnBlur
        UniversalKeyboardShortcutHandler.disableShortcuts()
        if (props.onEscape != null) {
            UniversalKeyboardShortcutHandler.registerEscapeHandler(props.onEscape)
        }
        UniversalKeyboardShortcutHandler.registerCtrlEnterKeyShortcut(handleCtrlEnter)
        textAreaRef.current?.focus()
    }

    override fun componentWillUnmount() {
        UniversalKeyboardShortcutHandler.enableShortcuts()
        UniversalKeyboardShortcutHandler.unregisterEscapeKeyShortcut()
        UniversalKeyboardShortcutHandler.unregisterCtrlEnterKeyShortcut()
    }

    private val handleCtrlEnter: () -> Unit = {
        val commentBody = textAreaRef.current?.value
        if (commentBody != null) {
            props.onInputCtrlEnter(commentBody)
            forceUpdate()
        }
    }

    private val enableKeyboardHandlerOnBlur: (FocusEvent) -> Unit = {
        UniversalKeyboardShortcutHandler.enableShortcuts()
    }

    private val disableKeyboardHandlerOnFocus: (FocusEvent) -> Unit = {
        UniversalKeyboardShortcutHandler.disableShortcuts()
    }
}

fun RBuilder.ctrlEnterInput(handler: CtrlEnterControlledInputProps.() -> Unit): ReactElement {
    return child(CtrlEnterControlledInput::class) {
        this.attrs(handler)
    }
}