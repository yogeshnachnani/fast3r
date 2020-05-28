package git.provider

import auth.OauthClient
import io.ktor.client.HttpClient
import io.ktor.client.features.json.defaultSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode


class GithubClient(
    private val oauthClient: OauthClient,
    private val httpClient: HttpClient
) {
    companion object {
        val ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json"
        val GITHUB_API_BASE_URL = "https://api.github.com/"
        val AUTH_HEADER_PREFIX = "token "
        val ORGS_PATH = "orgs/"
        val REPOS_PATH = "repos"
        val PULLS_PATH = "pulls"
        val jsonSerializer = defaultSerializer()
    }

    suspend fun getReposSummary(orgName: String): List<RepoSummary> {
        return httpClient.request<List<RepoSummary>>(buildRequest().apply {
            url("$GITHUB_API_BASE_URL$ORGS_PATH$orgName/${ REPOS_PATH }")
            method = HttpMethod.Get
        })
    }

    suspend fun listPullRequests(repoSummary: RepoSummary): List<PullRequestSummary> {
        return httpClient.request<List<PullRequestSummary>>(buildRequest().apply {
            url("$GITHUB_API_BASE_URL$REPOS_PATH/${repoSummary.full_name}/$PULLS_PATH")
            method = HttpMethod.Get
        })
    }

    suspend fun getPullRequestDetails(pullRequestSummary: PullRequestSummary): PullRequestDetails {
        return httpClient.request<PullRequestDetails>(buildRequest().apply {
            url("${pullRequestSummary._links.self}")
            method = HttpMethod.Get
        })
    }

    suspend fun updatePullRequest(pullRequestSummary: PullRequestSummary, pullRequestUpdate: PullRequestUpdate): PullRequestDetails {
        return httpClient.request<PullRequestDetails>(buildRequest().apply {
            url("${pullRequestSummary._links.self}")
            method = HttpMethod.Patch
            body = pullRequestUpdate
        })
    }

    /**
     * From GithubDocs
     * Lists a maximum of 250 commits for a pull request.
     * To receive a complete commit list for pull requests with more than 250 commits, use the Commit List API.
     */
    suspend fun listCommits(pullRequestSummary: PullRequestSummary): GithubCommit {
        return httpClient.request(buildRequest().apply {
            url("${pullRequestSummary._links.commits}")
            method = HttpMethod.Get
        })
    }

    suspend fun listFiles(pullRequestSummary: PullRequestSummary): List<PullRequestFileDetails> {
        return httpClient.request(buildRequest().apply {
            url("${pullRequestSummary._links.self}/files")
            method = HttpMethod.Get
        })
    }

    suspend fun isPrMerged(pullRequestSummary: PullRequestSummary): Boolean {
        val response: HttpResponse = httpClient.request(buildRequest().apply {
            url("${pullRequestSummary._links.self.href}/merge")
            method = HttpMethod.Get
        })
        return when(response.status) {
            HttpStatusCode.NoContent -> true
            HttpStatusCode.NotFound -> false
            else -> {
                throw RuntimeException("Unhandled status code ${response.status}")
            }
        }
    }

    suspend fun mergePullRequest(pullRequestSummary: PullRequestSummary, mergePullRequest: MergePullRequest): Boolean {
        val response: HttpResponse = httpClient.request(buildRequest().apply {
            url("${pullRequestSummary._links.self}/merge")
            method = HttpMethod.Put
            body = mergePullRequest
        })
        return when(response.status) {
            HttpStatusCode.OK -> true
            HttpStatusCode.MethodNotAllowed -> false
            HttpStatusCode.Conflict -> false
            else -> {
                throw RuntimeException("Unhandled status code ${response.status}")
            }
        }
    }

    private suspend fun buildRequest(): HttpRequestBuilder {
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Accept, ACCEPT_HEADER_VALUE)
            header(HttpHeaders.Authorization, "$AUTH_HEADER_PREFIX${oauthClient.getToken(emptyMap())}")
        }
    }
}