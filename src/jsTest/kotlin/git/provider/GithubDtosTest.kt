package git.provider

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * This weird import path is needed because `pull_request_example1.json` which is initially placed in
 * jsTest/resources/  directory is 'transferred to' 'build/js/processedResources/js/test folder
 * TODO: There must be a way to instruct gradle to put these files along with js test files before running the test. Figure that out
 */
@JsModule("../../../../processedResources/js/test/pull_request_example1.json")
external val pullRequestExample1Contents: dynamic

@JsModule("../../../../processedResources/js/test/pull_request_details.json")
external val pullRequestDetailsContents: dynamic

@JsModule("../../../../processedResources/js/test/pull_request_commitlist_entry.json")
external val pullRequestCommitListEntry: dynamic

@JsModule("../../../../processedResources/js/test/pull_request_filelist_entry.json")
external val pullRequestFileListEntry: dynamic
/**
 * Test Deserialisation / Serialisation of Github DTOs
 */
class GithubDtosTest {

    @Test
    fun shouldDeserialisePullRequestString() {
        val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
        val pullRequestSummary: PullRequestSummary = json.parse(PullRequestSummary.serializer(), JSON.stringify(pullRequestExample1Contents))
        assertEquals("Proposed changes to the website", pullRequestSummary.title)
        assertEquals(1, pullRequestSummary.assignees.size)
    }

    @Test
    fun shouldDeserialisePullRequestDetailsString() {
        val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
        val pullRequestDetails: PullRequestDetails = json.parse(PullRequestDetails.serializer(), JSON.stringify(pullRequestDetailsContents))
        assertEquals("Amazing new feature", pullRequestDetails.title)
        assertEquals(2, pullRequestDetails.assignees.size)
    }

    @Test
    fun shouldDeserialisePullRequestCommitListString() {
        val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
        val pullRequestCommit = json.parse(GithubCommit.serializer(), JSON.stringify(pullRequestCommitListEntry))
        assertEquals("Fix all the bugs", pullRequestCommit.commit.message)
        assertEquals(1, pullRequestCommit.parents.size)
    }

    @Test
    fun shouldDeserialisePullRequestFileListString() {
        val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
        val pullRequestFileDetails = json.parse(PullRequestFileDetails.serializer(), JSON.stringify(pullRequestFileListEntry))
        assertEquals("file1.txt", pullRequestFileDetails.filename)
        assertEquals(124L, pullRequestFileDetails.changes)
    }
}