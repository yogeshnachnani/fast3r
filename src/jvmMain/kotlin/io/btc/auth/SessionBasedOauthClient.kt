package io.btc.auth

import auth.OauthClient
import io.btc.supercr.db.OauthTokensRepository

class NotLoggedInException: RuntimeException("No user logged in")

class SessionBasedOauthClient constructor(
    private val oauthTokensRepository: OauthTokensRepository
): OauthClient {
    override suspend fun getToken(options: Map<String, Any>): String {
        return oauthTokensRepository.retrieveLatestToken()?.authToken ?: throw NotLoggedInException()
    }

    override suspend fun getUser(): String {
        return oauthTokensRepository.retrieveLatestToken()?.createdAt ?: throw NotLoggedInException()
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

}