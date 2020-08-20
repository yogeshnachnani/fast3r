package codereview

import DEFAULT_PORT
import git.provider.AccessTokenParams
import git.provider.AccessTokenRequest
import git.provider.AccessTokenResponse
import git.provider.AuthorizeRequest
import git.provider.InitiateLoginPacket
import git.provider.PullRequestSummary
import git.provider.RepoSummary
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class SuperCrClient(
    private val httpClient: HttpClient,
    hostName: String
) {
    companion object {
        private val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
    }

    private val baseUrl: String = "http://${hostName}:${DEFAULT_PORT}"

    suspend fun getAllProjects(): List<Project> {
        return httpClient.request<List<Project>>(buildRequest().apply {
            url("$baseUrl/projects/_all")
            method = HttpMethod.Get
        })
    }

    suspend fun fetchDetectedProjectsFor(repoSummaries: List<RepoSummary>): Map<RepoSummary, Project> {
        return httpClient.post<Map<RepoSummary, Project>> {
            body = repoSummaries
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("$baseUrl/repos/guess")
        }
    }

    suspend fun addProject(project: Project): Boolean {
        val response = httpClient.post<HttpResponse> {
            body = project
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("$baseUrl/projects")
        }
        return when(response.status) {
            HttpStatusCode.Created -> true
            HttpStatusCode.InternalServerError -> false
            HttpStatusCode.BadRequest -> false
            else -> {
                throw RuntimeException("Unhandled status code ${response.status}")
            }
        }
    }

    suspend fun startReview(project: Project, pullRequestSummary: PullRequestSummary): Pair<ReviewInfo?, String?> {
        val response: HttpResponse = httpClient.post<HttpResponse> {
            body = pullRequestSummary
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("$baseUrl/projects/${project.id}/review")
        }
        return when(response.status) {
            HttpStatusCode.Created -> Pair(json.parse(ReviewInfo.serializer(), response.readText()), null)
            else -> {
                Pair(null, "Unhandled status code ${response.status}")
            }
        }
    }

    suspend fun getReviewDiff(reviewInfo: ReviewInfo, pullRequestSummary: PullRequestSummary): FileDiffListV2 {
        return httpClient.request<FileDiffListV2>(buildRequest().apply {
            url("$baseUrl/projects/${reviewInfo.projectIdentifier}/review/${reviewInfo.rowId!!}")
            parameter("oldRef", pullRequestSummary.base.sha)
            parameter("newRef", pullRequestSummary.head.sha)
            method = HttpMethod.Get
        })
    }

    suspend fun postReview(reviewInfo: ReviewInfo, fileDiffListV2: FileDiffListV2): Pair<Boolean, String?> {
        val response: HttpResponse = httpClient.post<HttpResponse> {
            body = fileDiffListV2
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("$baseUrl/projects/${reviewInfo.projectIdentifier}/review/${reviewInfo.rowId!!}")
        }
        return when(response.status) {
            HttpStatusCode.OK -> Pair(true, null)
            else -> {
                Pair(false, "Unhandled status code ${response.status}")
            }
        }
    }

    suspend fun initiateLoginToGithub(initiateLoginPacket: InitiateLoginPacket): AuthorizeRequest {
        return httpClient.post<AuthorizeRequest> {
            body = initiateLoginPacket
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("$baseUrl/providers/github/initiate_login")
        }
    }

    suspend fun retrieveCurrentUser(): InitiateLoginPacket {
        return httpClient.request<InitiateLoginPacket>(buildRequest().apply {
            url("$baseUrl/providers/github/username")
            method = HttpMethod.Get
        })
    }

    suspend fun initDummyCreds() {
        httpClient.post<HttpResponse> {
            url("$baseUrl/providers/github/dummy_login")
        }
    }

    suspend fun loginToGithub(accessTokenParams: AccessTokenParams): AccessTokenResponse {
        return httpClient.post<AccessTokenResponse> {
            body = accessTokenParams
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("$baseUrl/providers/github/login")
        }
    }

    suspend fun githubAccessToken(): AccessTokenResponse {
        return httpClient.request<AccessTokenResponse>(buildRequest().apply {
            url("$baseUrl/providers/github/access_token")
            method = HttpMethod.Get
        })
    }

    private suspend fun buildRequest(): HttpRequestBuilder {
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }
    }
    private suspend fun buildPostRequest(requestBody: Any, path: String): HttpRequestBuilder {
        return HttpRequestBuilder().apply {
            method = HttpMethod.Post
            body = requestBody
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("$baseUrl/$path")
        }
    }
}