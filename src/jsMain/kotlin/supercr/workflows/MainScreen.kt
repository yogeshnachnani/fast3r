package supercr.workflows

import codereview.Project
import codereview.SuperCrClient
import git.provider.GithubClient
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import jsonParser
import kotlinx.browser.window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.styledDiv
import supercr.workflows.gettingstarted.components.loginComponent
import supercr.workflows.gettingstarted.components.repoInit
import supercr.workflows.login.github.GithubOauthClient
import supercr.workflows.overview.screens.overviewScreen

external interface MainScreenState: RState{
    var projects: List<Project>
    var isLoggedIn: Boolean
    var initialisedOnce: Boolean
}

external interface MainScreenProps: RProps {
}

class MainScreen : RComponent<MainScreenProps, MainScreenState>() {

    override fun RBuilder.render() {
        when {
            !state.isLoggedIn -> {
                loginComponent {
                    onLoginDone = receiveLoginDone
                    githubOauthClient = this@MainScreen.githubOauthClient
                }
            }
            state.projects.isEmpty() -> {
                if (state.initialisedOnce) {
                    repoInit {
                        githubClient = this@MainScreen.getGithubClient()
                        passProjectInfo = receiveProjects
                        superCrClient = this@MainScreen.superCrClient
                    }
                } else {
                    styledDiv {
                        + "Loading Projects.."
                    }
                }
            }
            else -> {
                overviewScreen {
                    projects = state.projects
                    getGithubClient = this@MainScreen.getGithubClient
                    superCrClient = this@MainScreen.superCrClient
                }
            }
        }
    }

    override fun componentDidMount() {
        GlobalScope.async(context = Dispatchers.Main) {
            superCrClient.getAllProjects()
                .let {
                    setState {
                        projects = it
                        initialisedOnce = true
                    }
                }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Something bad happened while listing projects in main screen")
                console.error(throwable)
            }
        }
    }

    override fun MainScreenState.init() {
        projects = emptyList()
        isLoggedIn = false
        initialisedOnce = false
    }

    private val receiveLoginDone: () -> Unit = {
        setState {
            isLoggedIn = true
        }
    }

    private val getGithubClient: () -> GithubClient = {
        GithubClient(
            oauthClient = this.githubOauthClient,
            httpClient = HttpClient() {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(json = jsonParser)
                }
            }
        )
    }

    private val receiveProjects: (List<Project>) -> Unit = { receivedProjects ->
        GlobalScope.async(context = Dispatchers.Main) {
            receivedProjects.map { project ->
                superCrClient.addProject(project)
            }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Something bad happened")
                console.error(throwable)
            }
        }
        setState {
            projects = receivedProjects
        }
    }

    private val superCrClient = SuperCrClient(
        httpClient = HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json = jsonParser)
            }
        },
        hostName = window.location.hostname
    )

    private val githubOauthClient = GithubOauthClient(
        superCrClient = this.superCrClient
    )

}

fun RBuilder.mainScreen(handler: MainScreenProps.() -> Unit) : ReactElement {
    return child(MainScreen::class) {
        this.attrs(handler)
    }
}