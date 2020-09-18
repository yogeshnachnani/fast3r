package supercr.workflows.login.github

import auth.OauthClient
import codereview.SuperCrClient
import git.provider.AccessTokenParams
import git.provider.AuthorizeRequest
import git.provider.InitiateLoginPacket
import kotlinx.browser.window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.w3c.dom.url.URLSearchParams

class GithubOauthClient constructor(
    private val superCrClient: SuperCrClient
): OauthClient {

    companion object {
        private const val baseUrl = "https://github.com/login/oauth"
    }

    fun webLogin(loginUserName: String) {
        GlobalScope.async(context = Dispatchers.Main) {
            val request = superCrClient.initiateLoginToGithub(initiateLoginPacket = InitiateLoginPacket(loginUserName))
            val requestAsParams = with(request) {
                "client_id=$client_id&redirect_uri=${window.location.href}&login=$login&scope=${scope}&state=$state"
            }
            window.location.href = "$baseUrl/authorize?$requestAsParams"
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Something bad happened while initiating login sequence with backend ")
                console.error(throwable)
            }
        }
    }

    suspend fun completeLoginIfApplicable(): Boolean {
        val query = URLSearchParams(window.location.search)
        val code = query.get("code")
        val state = query.get("state")
        return if (code != null && state != null) {
            /** TODO: Once loginToGithub succeeds, figure out a way to remove the [code] and [state] params from the window href */
            superCrClient.loginToGithub(AccessTokenParams(code = code, state = state))
            true
        } else {
            return try {
                superCrClient.githubAccessToken()
                true
            } catch (throwable: Throwable) {
                console.log("Could not determine login status ", throwable)
                false
            }

        }
    }

    suspend fun initDummyCreds(): Boolean {
        return try {
            superCrClient.initDummyCreds()
            true
        } catch (throwable: Throwable) {
            console.log("Could not determine login status ", throwable)
            false
        }
    }

    override suspend fun getToken(options: Map<String, Any>): String {
        return superCrClient.githubAccessToken().access_token
    }

    override suspend fun getUser(): String {
        return superCrClient.retrieveCurrentUser().login
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

}
