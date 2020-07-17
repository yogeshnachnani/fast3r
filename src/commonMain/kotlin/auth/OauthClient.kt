package auth

interface OauthClient {
    suspend fun getToken(options: Map<String, Any>): String
    suspend fun getUser(): String
    fun logout()
}

/** Basically uses a personal access token (or similar) and doesn't have anything to do with Oauth */
class DumbOauthClient(
    private val fixedAccessToken: String,
    private val fixedUser: String = "yogeshnachnani"
) : OauthClient {
    override suspend fun getToken(options: Map<String, Any>): String {
        return fixedAccessToken
    }

    override suspend fun getUser(): String {
        return fixedUser
    }

    override fun logout() {
        TODO("Ghanta logout karega tu")
    }
}