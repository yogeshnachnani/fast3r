package supercr.workflows.gettingstarted.components

import Paper
import PermIdentity
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
import kotlinx.html.js.onSubmitFunction
import kotlinx.html.onSubmit
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.createRef
import react.dom.defaultValue
import react.dom.span
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledInput
import styled.styledP
import styled.styledSpan
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
//            val loggedIn = false
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
        styledDiv {
            css {
                +ComponentStyles.loginScreen
            }
            styledDiv {
                css {
                    + ComponentStyles.loginScreenMessage
                }
                + "Enter your Github username"
            }
            styledDiv {
                css {
                    + ComponentStyles.loginScreenUsernameBoxContainer
                }
                styledDiv {
                    css {
                        + ComponentStyles.loginScreenUserIcon
                    }
                    PermIdentity {
                        attrs {
                            fontSize = "inherit"
                        }
                    }
                }
                styledInput {
                    css {
                        + ComponentStyles.loginGithubUsername
                    }
                    attrs {
                        autoFocus = true
                    }
                }
                styledSpan {
                    css {
                        + ComponentStyles.loginPressEnterLabel
                    }
                    + "Press Enter ↵"
                }
                enterActivatedButton {
                    label = "Go"
                    onSelected = handleEnter
                    buttonClazz = ComponentStyles.getClassName { ComponentStyles::loginGo }
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
