package git.provider

import auth.OauthClient
import codereview.Project
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode


class GithubClient(
    private val oauthClient: OauthClient,
    private val httpClient: HttpClient
) {
    companion object {
        private const val ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json"
        private const val GITHUB_API_BASE_URL = "https://api.github.com/"
        private const val AUTH_HEADER_PREFIX = "token "
        private const val ORGS_PATH = "orgs/"
        private const val REPOS_PATH = "repos"
        private const val PULLS_PATH = "pulls"
        private const val REVIEWS_PATH = "reviews"
        val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
    }

    suspend fun getReposSummary(orgName: String): List<RepoSummary> {
        return httpClient.request<List<RepoSummary>>(buildRequest().apply {
            url("$GITHUB_API_BASE_URL$ORGS_PATH$orgName/${ REPOS_PATH }")
            method = HttpMethod.Get
        })
    }

    suspend fun listPullRequests(
        project: Project,
        state: GithubPullRequestState = GithubPullRequestState.open,
        filterForCurrentUser: Boolean = true
    ): List<PullRequestSummary> {
        val pullRequests = httpClient.request<List<PullRequestSummary>>(buildRequest().apply {
            url("$GITHUB_API_BASE_URL$REPOS_PATH/${project.providerPath}/$PULLS_PATH")
            parameter("state", state.name)
            method = HttpMethod.Get
        })
        return if (filterForCurrentUser) {
            pullRequests.filter { pullRequest ->
                pullRequest.assignees.any { assignee -> assignee.login == oauthClient.getUser() }
                    || pullRequest.requested_reviewers.any { reviewer -> reviewer.login == oauthClient.getUser() }
            }
        } else {
            pullRequests
        }
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
            url("${pullRequestSummary._links.self!!.href}/merge")
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

    suspend fun listReviewsFor(pullRequestSummary: PullRequestSummary): List<Review> {
        return httpClient.request<List<Review>>(buildRequest().apply {
            url("${pullRequestSummary._links.self}/$REVIEWS_PATH")
            method = HttpMethod.Get
        })
    }

    suspend fun retrieveLatestInfoForReview(review: Review): Review {
        return httpClient.request<Review>(buildRequest().apply {
            url("${review.pull_request_url}/$REVIEWS_PATH/${review.id}")
            method = HttpMethod.Get
        })
    }

    suspend fun deleteReview(review: Review): Boolean {
        val response: HttpResponse = httpClient.request(buildRequest().apply {
            url("${review.pull_request_url}/$REVIEWS_PATH/${review.id}")
            method = HttpMethod.Delete
        })
        return when(response.status) {
            HttpStatusCode.OK -> true
            else -> {
                throw RuntimeException("Unhandled status code ${response.status}")
            }
        }
    }

    suspend fun listComments(review: Review): List<ReviewComment> {
        return httpClient.request<List<ReviewComment>>(buildRequest().apply {
            url("${review.pull_request_url}/$REVIEWS_PATH/${review.id}/comments")
            method = HttpMethod.Get
        })
    }

    suspend fun createReview(pullRequestSummary: PullRequestSummary, reviewPayload: ReviewPayload): Pair<Review?, String?> {
        val response = httpClient.post<HttpResponse> {
            body = reviewPayload
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("${pullRequestSummary._links.self}/$REVIEWS_PATH")
        }
        return when(response.status) {
            HttpStatusCode.OK -> Pair(json.parse(Review.serializer(), response.readText()), null)
            else -> Pair(null, response.readText())
        }
    }

    suspend fun updateOrSubmitReview(review: Review, reviewUpdatePayload: ReviewUpdatePayload): Boolean {
        val response = httpClient.post<HttpResponse> {
            body = reviewUpdatePayload
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("${review.pull_request_url}/$REVIEWS_PATH/${review.id}/events")
        }
        return when(response.status) {
            HttpStatusCode.OK -> true
            else -> false
        }
    }

    suspend fun dismissReview(review: Review, dismissPayload: ReviewDismissPayload): Boolean {
        val response = httpClient.put<HttpResponse> {
            body = dismissPayload
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("${review.pull_request_url}/$REVIEWS_PATH/${review.id}/dismissals")
        }
        return when(response.status) {
            HttpStatusCode.OK -> true
            else -> false
        }
    }

    suspend fun listComments(pullRequestSummary: PullRequestSummary): List<PullRequestReviewComment> {
        return httpClient.request<List<PullRequestReviewComment>>(buildRequest().apply {
            url("${pullRequestSummary._links.review_comments}")
            method = HttpMethod.Get
        })
    }

    private suspend fun buildRequest(): HttpRequestBuilder {
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Accept, ACCEPT_HEADER_VALUE)
            header(HttpHeaders.Authorization, "$AUTH_HEADER_PREFIX${oauthClient.getToken(emptyMap())}")
        }
    }
}