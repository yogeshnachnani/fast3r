package git.provider

import auth.DumbOauthClient
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import runTest
import kotlin.js.Date
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.measureTime

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
     */
    @Test
    @Ignore
    fun tempTestForGithubClient() = runTest {
        try {
            val reposSummary = githubClient.getReposSummary("theboringtech")
            assertEquals(2, reposSummary.size)
            val websiteRepo = reposSummary.first { it.name.contains("github.io") }
            val pullRequests = githubClient.listPullRequests(websiteRepo)
            val prMerged = githubClient.isPrMerged(pullRequests.first())
            assertFalse(prMerged)
            val pulLRequestDetails = githubClient.getPullRequestDetails(pullRequests.first())
            assertTrue(pulLRequestDetails.mergeable)
        } catch (e: Exception) {
            if (e.message?.contains("401 Unauthorized") == true) {
                println("The test by default doesn't have an access token. Add an access token while creating github client")
                throw RuntimeException("Check access token on github client")
            }
            throw RuntimeException(e)
        }
    }

}