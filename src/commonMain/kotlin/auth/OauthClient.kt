package auth

interface OauthClient {
    fun getToken(options: Map<String, String>): String
    fun getUser(): String
    fun logout()
}

/** Basically uses a personal access token (or similar) and doesn't have anything to do with Oauth */
class DumbOauthClient(
    private val fixedAccessToken: String,
    private val fixedUser: String = "yogeshnachnani"
) : OauthClient {
    override fun getToken(options: Map<String, String>): String {
        return fixedAccessToken
    }

    override fun getUser(): String {
        return fixedUser
    }

    override fun logout() {
        TODO("Ghanta logout karega tu")
    }
}