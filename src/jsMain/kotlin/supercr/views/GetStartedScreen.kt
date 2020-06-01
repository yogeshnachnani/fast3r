package supercr.views

import Button
import Grid
import ListItem
import MaterialUIList
import Paper
import auth.DumbOauthClient
import git.provider.GithubClient
import git.provider.RepoSummary
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
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
import react.setState
import styled.css
import styled.styledInput

external interface GettingStartedScreenState: RState {
    var isLoggedIn: Boolean
    var accessToken: String
}

class GetStartedScreen: RComponent<RProps, GettingStartedScreenState>() {

    override fun GettingStartedScreenState.init() {
        isLoggedIn = false
        accessToken = ""
    }

    override fun RBuilder.render() {
        if (!state.isLoggedIn) {
            loginComponent {
                onLoginButtonPressed = { enteredAccessToken ->
                    setState {
                        isLoggedIn = true
                        accessToken = enteredAccessToken
                    }
                }
            }
        } else {
            repoInit {
                githubClient = GithubClient(
                    oauthClient = DumbOauthClient(state.accessToken),
                    httpClient = HttpClient() {
                        install(JsonFeature) {
                            serializer = KotlinxSerializer(json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)))
                        }
                    }
                )
                gotoReviewScreen = {
                    console.log("Will go to next screen now")
                }
            }
        }
    }

}

fun RBuilder.getStartedScreen(handler: RProps.() -> Unit): ReactElement {
    return child(GetStartedScreen::class) {
        this.attrs(handler)
    }
}

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
                            xs = 12
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
                            xs = 4
                        }
                    }
                    Grid {
                        attrs {
                            item = true
                            container = false
                            xs = 4
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
                            xs = 4
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

interface RepoInitProps: RProps {
    var githubClient: GithubClient
    var gotoReviewScreen: () -> Unit
}

interface RepoInitState: RState {
    var repoList: List<RepoSummary>
    var showNextButton: Boolean
}

class RepoInitComponent: RComponent<RepoInitProps, RepoInitState>() {
    override fun RepoInitState.init() {
        repoList = emptyList()
        showNextButton = false
    }

    override fun RBuilder.render() {
        MaterialUIList {
            state.repoList.map {
                repoComponent {
                    repoSummary = it
                    onPathSelect = handleRepoSelection
                }
            }
        }
        if (state.showNextButton) {
            Button {
                attrs {
                    variant = "contained"
                    color = "primary"
                    onClick= props.gotoReviewScreen
                }
                + "Done"
            }
        }
    }

    override fun componentDidMount() {
        fetchRepos()
    }

    /**
     * TODO: Add some validations to check if the repo being selected is correct or no
     *  ** Selected Path should be a git repo
     *  ** Selected Path must point to the same origin (getOriginURL().contains(repoName))
     */
    private val handleRepoSelection: (String) -> Boolean = { givenPath ->
        setState {
            showNextButton = true
        }
        true
    }

    private fun fetchRepos() {
        GlobalScope.async {
            props.githubClient.getReposSummary("theboringtech")
                .let {
                    setState {
                        repoList = it
                    }
                }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Something bad happened")
                console.error(throwable)
            }
        }
    }
}

fun RBuilder.repoInit(handler: RepoInitProps.() -> Unit): ReactElement {
    return child(RepoInitComponent::class) {
        this.attrs(handler)
    }
}

external interface RepoComponentProps: RProps {
    var repoSummary: RepoSummary
    var onPathSelect: (String) -> Boolean
}

external interface RepoComponentState: RState {
    var isProcessed: Boolean
}

class RepoComponent: RComponent<RepoComponentProps, RepoComponentState>() {
    private val inputFileRef = createRef<HTMLInputElement>()
    private val handleSelection =  {
        console.log("Clicked ${props.repoSummary.full_name}")
//        inputFileRef.current!!.click() TODO See below. Below if condition is just a hack to select the 'right folder'
        val markProcessed = if (props.repoSummary.full_name == "theboringtech/btcmain") {
            props.onPathSelect("/home/yogesh/work/btc")
        } else {
            props.onPathSelect("/home/yogesh/work/theboringtech.github.io")
        }
        setState {
            isProcessed = markProcessed
        }
    }

    override fun RepoComponentState.init() {
        isProcessed = false
    }

    override fun RBuilder.render() {
        ListItem {
            attrs {
                button = true
                alignItems = "center"
                divider = true
                onClick = handleSelection
                disabled = state.isProcessed
            }
            p {
                +props.repoSummary.full_name
            }
            /** TODO: Make this work once we switch to electron or Kvision. Right now it just sits there - never used */
            styledInput {
                css {
                    display = Display.none
                }
                attrs {
                    ref = inputFileRef
                }
                attrs.onChangeFunction = { event ->
                    console.log("Got the value as ${inputFileRef.current?.files}")
                }
            }
        }
    }

}

private fun RBuilder.repoComponent(handler: RepoComponentProps.() -> Unit): ReactElement {
    return child(RepoComponent::class) {
        this.attrs(handler)
    }
}

