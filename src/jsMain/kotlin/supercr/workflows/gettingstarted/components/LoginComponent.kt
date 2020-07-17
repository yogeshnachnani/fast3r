package supercr.workflows.gettingstarted.components

import Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledInput
import styled.styledP
import supercr.css.ComponentStyles
import supercr.kb.components.enterActivatedButton
import supercr.workflows.login.github.GithubOauthClient

interface LoginComponentProps: RProps {
    var onLoginDone: () -> Unit
    var githubOauthClient: GithubOauthClient
}

interface LoginComponentState: RState {
    var isLoggedIn: Boolean
}

class LoginComponent: RComponent<LoginComponentProps, LoginComponentState>() {
    private val githubUsernameInputRef = createRef<HTMLInputElement>()

    override fun LoginComponentState.init() {
        isLoggedIn = false
    }

    override fun RBuilder.render() {
        if (state.isLoggedIn) {
            styledDiv {
                styledP {
                    +"Logged in.. Redirecting"
                }
            }
        } else {
            renderLoginButton()
        }
    }

    override fun componentDidMount() {
        GlobalScope.async(context = Dispatchers.Main) {
            val loggedIn = props.githubOauthClient.completeLoginIfApplicable()
            setState {
                isLoggedIn = loggedIn
            }
            if (loggedIn) {
                props.onLoginDone()
            }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Something bad happened while checking if user is logged in within LoginComponent")
                console.error(throwable)
            }
        }
    }

    private fun RBuilder.renderLoginButton() {
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
                + "Welcome to the Fast3r. Please enter your github username"
            }
            styledInput(type = InputType.text) {
                css {
                    display = Display.block
                    marginTop = 15.px
                }
                ref = githubUsernameInputRef
                attrs.defaultValue = "yogeshnachnani"
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
        props.githubOauthClient.webLogin(githubUsernameInputRef.current!!.value)
    }
}
fun RBuilder.loginComponent(handler: LoginComponentProps.() -> Unit): ReactElement {
    return child(LoginComponent::class) {
        this.attrs(handler)
    }
}
