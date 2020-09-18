package io.btc.supercr.api

import YOGESHNACHNANI_GITHUB_USERNAME
import YOGESHNACHNANI_PUBLIC_REPO_PERSONAL_ACCESS_TOKEN
import git.provider.AccessTokenParams
import git.provider.AccessTokenRequest
import git.provider.AccessTokenResponse
import git.provider.AuthorizeRequest
import git.provider.InitiateLoginPacket
import io.btc.supercr.db.OauthTokenInfo
import io.btc.supercr.db.OauthTokensRepository
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import jsonParser
import org.slf4j.LoggerFactory
import java.util.*

class OauthApi constructor(
    routing: Routing,
    private val oauthTokensRepository: OauthTokensRepository
): ApiController(routing) {
    companion object {
        private val logger = LoggerFactory.getLogger(OauthApi::class.java)
        private const val githubBaseUrl = "https://github.com/login/oauth"
    }
    private val httpClient = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json = jsonParser)
        }
    }

    override fun initRoutes(routing: Routing) {
        routing {
            route("/providers")  {
                route("{provider_name}") {
                    post("dummy_login") {
                        val randomString = "dummy_${UUID.randomUUID().toString().replace("-", "")}"
                        oauthTokensRepository.createNewTokenRequest(YOGESHNACHNANI_GITHUB_USERNAME, randomString)
                        val existingLogin = oauthTokensRepository.retrieveExistingLogin(state = randomString)!!
                        oauthTokensRepository.updateAuthToken(
                            existingLogin.copy(authToken = YOGESHNACHNANI_PUBLIC_REPO_PERSONAL_ACCESS_TOKEN, scope = "repo", tokenType = "bearer")
                        )
                        call.respond(HttpStatusCode.Created)
                    }
                    post("initiate_login") {
                        val initiatePacket = call.receive<InitiateLoginPacket>()
                        val randomString = UUID.randomUUID().toString().replace("-", "")
                        val request = AuthorizeRequest(login = initiatePacket.login, state = randomString)
                        oauthTokensRepository.createNewTokenRequest(initiatePacket.login, randomString)
                        logger.debug("UI should use the following auth request {}", request)
                        call.respond(request)
                    }
                    post("login"){
                        with(call.receive<AccessTokenParams>()) {
                            val existingLogin = oauthTokensRepository.retrieveExistingLogin(state = state)
                            when {
                                existingLogin == null -> {
                                    call.respond(HttpStatusCode.BadRequest, "Attempting to login without any prior login initiated")
                                }
                                existingLogin.authToken != null -> {
                                    call.respond(HttpStatusCode.OK, existingLogin.toGithubAccessTokenResponse())
                                }
                                else -> {
                                    logger.debug("Found existing login for {} , will auth with github", existingLogin.login)
                                    val response = httpClient.post<HttpResponse> {
                                        body = AccessTokenRequest(code = code, state = state)
                                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                                        url("$githubBaseUrl/access_token")
                                    }
                                    logger.debug("Received response from github with code : {}", response.status)
                                    with(jsonParser.decodeFromString(AccessTokenResponse.serializer(), response.readText())) {
                                        oauthTokensRepository.updateAuthToken(
                                            existingLogin.copy(authToken = access_token, scope = scope, tokenType = token_type)
                                        )
                                        call.respond(this)
                                    }
                                }
                            }
                        }
                    }
                    get("access_token") {
                        val latestToken = oauthTokensRepository.retrieveLatestToken()
                        if (latestToken != null) {
                            call.respond(latestToken.toGithubAccessTokenResponse())
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                    get("username") {
                        val latestToken = oauthTokensRepository.retrieveLatestToken()
                        if (latestToken != null) {
                            call.respond(InitiateLoginPacket(login = latestToken.login))
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                }
            }
        }
    }

    private fun OauthTokenInfo.toGithubAccessTokenResponse(): AccessTokenResponse {
        return AccessTokenResponse(
            access_token = this.authToken!!,
            scope = this.scope!!,
            token_type = this.tokenType!!
        )
    }

}
