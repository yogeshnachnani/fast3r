package supercr.workflows

import auth.DumbOauthClient
import codereview.Project
import codereview.SuperCrClient
import git.provider.GithubClient
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import supercr.workflows.gettingstarted.screens.getStartedScreen
import supercr.workflows.overview.screens.overviewScreen

external interface MainScreenState: RState{
    var projects: List<Project>
    var accessToken: String
}

external interface MainScreenProps: RProps {

}

class MainScreen : RComponent<MainScreenProps, MainScreenState>() {
    override fun RBuilder.render() {
        if(state.projects.isEmpty()) {
            getStartedScreen { 
                passAccessToken = receiveGithubAccessToken
                passProjects = receiveProjects
                superCrClient = this@MainScreen.superCrClient
            }
        } else {
            overviewScreen {
                projects = state.projects
                getGithubClient = this@MainScreen.getGithubClient
            }
        }
    }

    override fun componentDidMount() {
        GlobalScope.async(context = Dispatchers.Main) {
            superCrClient.getAllProjects()
                .let {
                    setState {
                        projects = it
                    }
                }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Something bad happened")
                console.error(throwable)
            }
        }
    }

    override fun MainScreenState.init() {
        projects = emptyList()
        accessToken = "4da05160ff8311a45f31fe8fd6ab374b8a6f9fe9"
    }

    private val receiveGithubAccessToken: (String) -> Unit = { receivedAccessToken ->
        setState {
            accessToken = receivedAccessToken
        }
    }

    private val getGithubClient: () -> GithubClient = {
        GithubClient(
            oauthClient = DumbOauthClient(state.accessToken),
            httpClient = HttpClient() {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)))
                }
            }
        )
    }

    private val receiveProjects: (List<Project>) -> Unit = { receivedProjects ->
        setState {
            projects += receivedProjects
        }
    }

    private val superCrClient = SuperCrClient(
        httpClient = HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)))
            }
        }
    )

}

fun RBuilder.mainScreen(handler: MainScreenProps.() -> Unit) : ReactElement {
    return child(MainScreen::class) {
        this.attrs(handler)
    }
}