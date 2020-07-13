package supercr.kb.components

import Button
import Grid
import kotlinx.css.color
import kotlinx.css.marginTop
import kotlinx.css.paddingTop
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.div
import react.dom.strong
import styled.css
import styled.styledDiv
import styled.styledSpan
import supercr.css.Colors
import supercr.kb.UniversalKeyboardShortcutHandler

external interface EnterActivatedButtonProps : RProps {
    var onSelected: () -> Unit
    var label: String
}

external interface EnterActivatedButtonState : RState {

}

class EnterActivatedButton : RComponent<EnterActivatedButtonProps, EnterActivatedButtonState>() {
    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                justify = "space-evenly"
                item = false
            }
            Grid{
                attrs {
                    item = true
                    container = false
                }
                Button {
                    attrs {
                        variant = "contained"
                        color = "primary"
                        onClick = props.onSelected
                    }
                    + props.label
                }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                }
                styledDiv {
                    css {
                        color = Colors.baseText
                        paddingTop = 10.px
                    }
                    + "press "
                    strong {
                        + "Enter ↵"
                    }
                }
            }
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