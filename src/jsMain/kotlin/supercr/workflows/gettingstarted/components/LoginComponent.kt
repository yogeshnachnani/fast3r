package supercr.workflows.gettingstarted.components

import Grid
import PermIdentity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.Display
import kotlinx.css.TextAlign
import kotlinx.css.backgroundColor
import kotlinx.css.basis
import kotlinx.css.display
import kotlinx.css.flexBasis
import kotlinx.css.flexGrow
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.textAlign
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
import supercr.css.Colors
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
            Grid {
                attrs {
                    container = true
                    item = false
                    justify = "center"
                    alignItems = "center"
                    className = ComponentStyles.getClassName { ComponentStyles::loginScreen }
                }
                Grid {
                    attrs {
                        item = true
                        container = false
                        md = 6
                        lg = 6
                        xl = 3
                    }
                    renderLoginButton()
                    renderDemoLoginButton()
                }
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
            styledDiv {
                css {
                    + ComponentStyles.loginScreenOrLine
                }
            }
            styledDiv {
                css {
                    + ComponentStyles.loginScreenOrText
                }
                + " Or "
            }
            styledDiv {
                css {
                    + ComponentStyles.loginScreenOrLine
                }
            }
        }
        styledDiv {
            css {
                + ComponentStyles.loginScreenDemoCredentialsContainer
            }
            ctrlEnterAtivatedButton {
                attrs {
                    label = "Sign in using demo credentials"
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
            styledDiv {
                css {
                    display = Display.flex
                    flexGrow = 2.0
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
            }
            styledDiv {
                css {
                    display = Display.inlineFlex
                }
                styledP {
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
