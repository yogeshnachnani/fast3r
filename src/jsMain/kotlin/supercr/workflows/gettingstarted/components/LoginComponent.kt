package supercr.workflows.gettingstarted.components

import Button
import Grid
import Paper
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

interface LoginComponentProps: RProps {
    var onLoginButtonPressed: (String) -> Unit
}

class LoginComponent: RComponent<LoginComponentProps, RState>() {
    private val personalAccessTokenInputRef = createRef<HTMLInputElement>()

    override fun RBuilder.render() {
        Grid {
            attrs {
                item = false
                container = true
                alignItems = "center"
                direction = "row"
                justify = "center"
                spacing = 4
            }
        }
        Grid {
            attrs {
                item = true
                container = false
            }
            Paper {
                attrs {
                    square = true
                    elevation = 3
                }
                Grid {
                    attrs {
                        item = false
                        container = true
                        alignItems = "center"
                        direction = "row"
                        justify = "center"
                        spacing = 2
                    }
                    Grid {
                        attrs {
                            item = true
                            container = false
                            md = 12
                        }
                        p {
                            + "Welcome to the fastest CR experience. Lets Get Started"
                        }
                        input(type = InputType.password) {
                            ref = personalAccessTokenInputRef
                            attrs.placeholder = "Enter your github personal access token"
                            attrs.defaultValue = "4da05160ff8311a45f31fe8fd6ab374b8a6f9fe9"
                        }
                    }
                    Grid {
                        attrs {
                            item = true
                            container = false
                            md = 4
                        }
                    }
                    Grid {
                        attrs {
                            item = true
                            container = false
                            md = 4
                        }
                        Button {
                            attrs {
                                variant = "contained"
                                color = "primary"
                                onClick= {
                                    props.onLoginButtonPressed(personalAccessTokenInputRef.current!!.value)
                                }
                            }
                            + "Login"
                        }
                    }
                    Grid {
                        attrs {
                            item = true
                            container = false
                            md = 4
                        }
                    }
                }
            }
        }
    }
}
fun RBuilder.loginComponent(handler: LoginComponentProps.() -> Unit): ReactElement {
    return child(LoginComponent::class) {
        this.attrs(handler)
    }
}
