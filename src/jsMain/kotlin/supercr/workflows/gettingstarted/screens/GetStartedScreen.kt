package supercr.workflows.gettingstarted.screens

import auth.DumbOauthClient
import codereview.Project
import codereview.SuperCrClient
import git.provider.GithubClient
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import supercr.workflows.gettingstarted.components.loginComponent
import supercr.workflows.gettingstarted.components.repoInit

external interface GettingStartedScreenProps: RProps {
    var passAccessToken: (String) -> Unit
    var passProjects: (List<Project>) -> Unit
    var superCrClient: SuperCrClient
}

external interface GettingStartedScreenState: RState {
    var isLoggedIn: Boolean
    var accessToken: String
    var githubClient: GithubClient
}

/**
 * For first time users. 2 functionalities
 * (a) Login to specific provider
 * (b) Setup at least 1 [Project] (map provider path to local path)
 */
class GetStartedScreen: RComponent<GettingStartedScreenProps, GettingStartedScreenState>() {

    override fun GettingStartedScreenState.init() {
        isLoggedIn = false
        accessToken = ""
    }

    override fun RBuilder.render() {
        if (!state.isLoggedIn) {
            loginComponent {
                onLoginButtonPressed = handleLogin
            }
        } else {
            repoInit {
                githubClient = state.githubClient
                passProjectInfo = props.passProjects
                superCrClient = props.superCrClient
            }
        }
    }

    private val handleLogin: (String) -> Unit = { enteredAccessToken ->
        val newGithubClient = GithubClient(
            oauthClient = DumbOauthClient(enteredAccessToken),
            httpClient = HttpClient() {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)))
                }
            }
        )
        props.passAccessToken(enteredAccessToken)
        setState {
            isLoggedIn = true
            accessToken = enteredAccessToken
            githubClient = newGithubClient
        }
    }

}

fun RBuilder.getStartedScreen(handler: GettingStartedScreenProps.() -> Unit): ReactElement {
    return child(GetStartedScreen::class) {
        this.attrs(handler)
    }
}




