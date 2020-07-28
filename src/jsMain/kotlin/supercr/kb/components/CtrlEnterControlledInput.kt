package supercr.kb.components

import OutlinedInput
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width
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
import react.dom.span
import styled.css
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.ComponentStyles
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
//        styledDiv {
//            css {
//                display = Display.block
//                margin(0.px)
//                padding(0.px)
//                width = 100.pct
//            }
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
            styledDiv {
                css {
                    + ComponentStyles.ctrlEnterSendHelpMessage
                }
                span {
                    + "Send: ctrl+enter"
                }
            }
//        }
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