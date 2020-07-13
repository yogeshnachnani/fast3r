package supercr.workflows.gettingstarted.components

import Button
import Grid
import Paper
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.css.marginTop
import kotlinx.css.maxWidth
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.createRef
import react.dom.defaultValue
import react.dom.input
import react.dom.p
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledInput
import styled.styledP
import supercr.css.ComponentStyles
import supercr.kb.components.enterActivatedButton

interface LoginComponentProps: RProps {
    var onLoginButtonPressed: (String) -> Unit
}

class LoginComponent: RComponent<LoginComponentProps, RState>() {
    private val personalAccessTokenInputRef = createRef<HTMLInputElement>()

    override fun RBuilder.render() {
        Paper {
            attrs {
                square = true
                elevation = 3
                className = ComponentStyles.getClassName { ComponentStyles::loginComponentPaper }
            }
            styledP {
                css {
                    marginTop = 15.px
                }
                + "Welcome to the Fast3r. Please enter your github personal access token"
            }
            styledInput(type = InputType.password) {
                css {
                    display = Display.block
                    marginTop = 15.px
                }
                ref = personalAccessTokenInputRef
                attrs.defaultValue = "417a060c57755029fba76f508f87deaec5442470"
            }
            styledDiv {
                css {
                    width = 190.px
                    maxWidth = 190.px
                    marginTop = 15.px
                }
                enterActivatedButton {
                    label = "Go"
                    onSelected = handleEnter
                }
            }
        }
    }

    private val handleEnter : () ->  Unit = {
        props.onLoginButtonPressed(personalAccessTokenInputRef.current!!.value)
    }
}
fun RBuilder.loginComponent(handler: LoginComponentProps.() -> Unit): ReactElement {
    return child(LoginComponent::class) {
        this.attrs(handler)
    }
}
