package git.provider

import auth.DumbOauthClient
import codereview.Project
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class GithubClientTest {
    val githubClient = GithubClient(
        oauthClient = DumbOauthClient(""),
        httpClient = HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)))
            }
        }
    )

    /**
     * This will be re-written as part of a more elaborate e2e test
     * Note: We have a lot of println statements to help zero in on exact call where the error occurs
     */
    @Test
    @Ignore
    fun tempTestForGithubClient() = runTest {
        try {
            val reposSummary = githubClient.getReposSummary("theboringtech")
            assertEquals(2, reposSummary.size)
            val websiteRepo = reposSummary.first { it.name.contains("github.io") }
            val project = Project("/home/yogesh/work/theboringtech.github.io", websiteRepo.full_name, websiteRepo.name)
            println("Will Fetch pull request for $project")
            val pullRequests = githubClient.listPullRequests(project)
            println("Pull requests fetched. Now, will check if pr is merged")
            val prMerged = githubClient.isPrMerged(pullRequests.first())
            println("Retrieved prMergedValue")
            assertFalse(prMerged)
            println("Will now fetch pull request details")
            val pulLRequestDetails = githubClient.getPullRequestDetails(pullRequests.first())
            assertTrue(pulLRequestDetails.mergeable)
        } catch (e: Exception) {
            if (e.message?.contains("401 Unauthorized") == true) {
                println("The test by default doesn't have an access token. Add an access token while creating github client")
                throw RuntimeException("Check access token on github client")
            }
            throw e
        }
    }

    @Test
    @Ignore
    fun testGithubReviews() = runTest {
        try {
            val project = Project("/home/yogesh/work/theboringtech.github.io", "theboringtech/theboringtech.github.io", "")
            val pullRequest = githubClient.listPullRequests(project).find { it.number == 2 }
            println("Pull requests fetched. ")
            val reviews = githubClient.listReviewsFor(pullRequest!!)
            println("reviews fetched")
            assertTrue(reviews.any { it.body.contains("making another comment") })
            val pullRequestComments = githubClient.listComments(pullRequest!!)
            println("PullRequestsComments fetched :  $pullRequestComments")
            assertTrue(pullRequestComments.size >= 2)
        } catch (e: Exception) {
            if (e.message?.contains("401 Unauthorized") == true) {
                println("The test by default doesn't have an access token. Add an access token while creating github client")
                throw RuntimeException("Check access token on github client")
            }
            throw e
        }
    }

}