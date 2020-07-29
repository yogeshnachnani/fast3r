package supercr.workflows.gettingstarted.components

import PermIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.Display
import kotlinx.css.display
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.createRef
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledInput
import styled.styledP
import styled.styledSpan
import supercr.css.ComponentStyles
import supercr.kb.components.ctrlEnterAtivatedButton
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
            styledDiv {
                css {
                    +ComponentStyles.loginScreen
                }
                renderLoginButton()
                renderDemoLoginButton()
            }
        }
    }

    override fun componentDidMount() {
        checkAndSetLoginStatus {
            props.githubOauthClient.completeLoginIfApplicable()
//            disableLogin()
        }
    }

    private fun RBuilder.renderDemoLoginButton() {
        styledDiv {
            css {
                + ComponentStyles.loginScreenOrMessage
            }
            styledP {
                css {
                    display = Display.inlineBlock
                }
                + "OR"
            }
            ctrlEnterAtivatedButton {
                attrs {
                    label = "Use Demo Credentials"
                    enterTextOnLeft = false
                    onSelected = handleCtrlEnter
                    buttonClazz = ComponentStyles.getClassName { ComponentStyles::loginScreenDemoButton  }
                    enterTextClazz = ComponentStyles.getClassName { ComponentStyles::loginScreenPressCtrlEnterLabel }
                }
            }
        }
    }

    private fun RBuilder.renderLoginButton() {
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
                ref = githubUsernameInputRef
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

    private val handleEnter : () ->  Unit = {
        props.githubOauthClient.webLogin(githubUsernameInputRef.current!!.value)
    }

    private val checkAndSetLoginStatus: (suspend () -> Boolean) -> Unit = { loginMethod ->
        GlobalScope.async(context = Dispatchers.Main) {
            val loggedIn = loginMethod.invoke()
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

    private val handleCtrlEnter: () -> Unit = {
        checkAndSetLoginStatus {
            loginViaDemoCreds()
        }
    }

    private val disableLogin: suspend () -> Boolean = {
        false
    }

    private val loginViaDemoCreds: suspend () -> Boolean = {
        props.githubOauthClient.initDummyCreds()
    }
}
fun RBuilder.loginComponent(handler: LoginComponentProps.() -> Unit): ReactElement {
    return child(LoginComponent::class) {
        this.attrs(handler)
    }
}
