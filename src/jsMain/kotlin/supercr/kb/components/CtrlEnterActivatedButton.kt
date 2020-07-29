package supercr.kb.components

import Button
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.html.DIV
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv
import styled.styledSpan
import supercr.kb.UniversalKeyboardShortcutHandler

external interface CtrlEnterActivatedButtonProps : RProps {
    var onSelected: () -> Unit
    var label: String
    var buttonClazz: String
    var enterTextClazz: String
    var enterTextOnLeft: Boolean
}

external interface CtrlEnterActivatedButtonState : RState {

}

class CtrlEnterActivatedButton : RComponent<CtrlEnterActivatedButtonProps, CtrlEnterActivatedButtonState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.inlineBlock
            }
            if (props.enterTextOnLeft) {
                renderKbShortcutHelpText()
            }
            Button {
                attrs {
                    variant = "contained"
                    onClick = props.onSelected
                    className = props.buttonClazz!!
                }
                + props.label
            }
            if (!props.enterTextOnLeft) {
                renderKbShortcutHelpText()
            }
        }
    }

    private fun StyledDOMBuilder<DIV>.renderKbShortcutHelpText() {
        styledSpan {
            css {
                classes.add(props.enterTextClazz)
            }
            +"Press Ctrl+Enter"
        }
    }

    override fun componentDidMount() {
        UniversalKeyboardShortcutHandler.registerCtrlEnterKeyShortcut(props.onSelected)
    }

    override fun componentWillUnmount() {
        UniversalKeyboardShortcutHandler.unregisterCtrlEnterKeyShortcut()
    }
}

fun RBuilder.ctrlEnterAtivatedButton(handler: CtrlEnterActivatedButtonProps.() -> Unit): ReactElement {
    return child(CtrlEnterActivatedButton::class) {
        this.attrs(handler)
    }
}