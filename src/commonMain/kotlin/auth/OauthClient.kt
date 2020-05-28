package auth

interface OauthClient {
    fun getToken(options: Map<String, String>): String
    fun logout()
}

/** Basically uses a personal access token (or similar) and doesn't have anything to do with Oauth */
class DumbOauthClient(
    val fixedAccessToken: String
) : OauthClient {
    override fun getToken(options: Map<String, String>): String {
        return fixedAccessToken
    }

    override fun logout() {
        TODO("Ghanta logout karega tu")
    }
}