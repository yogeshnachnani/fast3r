package supercr.kb.components

import Button
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import supercr.kb.UniversalKeyboardShortcutHandler

external interface EnterActivatedButtonProps : RProps {
    var onSelected: () -> Unit
    var label: String
    var buttonClazz: String?
}

external interface EnterActivatedButtonState : RState {

}

class EnterActivatedButton : RComponent<EnterActivatedButtonProps, EnterActivatedButtonState>() {
    override fun RBuilder.render() {
        renderButtonUsingProps()
    }

    private fun RBuilder.renderButtonUsingProps() {
        Button {
            attrs {
                variant = "contained"
                onClick = props.onSelected
                className = props.buttonClazz!!
            }
            + props.label
        }
    }

    override fun componentDidMount() {
        UniversalKeyboardShortcutHandler.registerEnterKeyShortcut(props.onSelected)
    }

    override fun componentWillUnmount() {
        UniversalKeyboardShortcutHandler.unregisterEnterKeyShortcut()
    }
}

fun RBuilder.enterActivatedButton(handler: EnterActivatedButtonProps.() -> Unit): ReactElement {
    return child(EnterActivatedButton::class) {
        this.attrs(handler)
    }
}