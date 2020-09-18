package git.provider

import jsonParser
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

@JsModule("../../../../processedResources/js/test/pull_request_example2.json")
external val pullRequestExample2WithoutAssigneeContents: dynamic

@JsModule("../../../../processedResources/js/test/pull_request_details.json")
external val pullRequestDetailsContents: dynamic

@JsModule("../../../../processedResources/js/test/pull_request_commitlist_entry.json")
external val pullRequestCommitListEntry: dynamic

@JsModule("../../../../processedResources/js/test/pull_request_filelist_entry.json")
external val pullRequestFileListEntry: dynamic

@JsModule("../../../../processedResources/js/test/issue_comment_example1.json")
external val issueComment: dynamic

@JsModule("../../../../processedResources/js/test/review_example1.json")
external val review: dynamic

@JsModule("../../../../processedResources/js/test/review_comment_example1.json")
external val reviewComment: dynamic

@JsModule("../../../../processedResources/js/test/pull_request_comment_example1.json")
external val pullRequestComment: dynamic
/**
 * Test Deserialisation / Serialisation of Github DTOs
 */
class GithubDtosTest {

    @Test
    fun shouldDeserialisePullRequestString() {
        val pullRequestSummary: PullRequestSummary = jsonParser.decodeFromString(PullRequestSummary.serializer(), JSON.stringify(pullRequestExample1Contents))
        assertEquals("Proposed changes to the website", pullRequestSummary.title)
        assertEquals(1, pullRequestSummary.assignees.size)

        val pullRequestWithoutAssigneeSummary: PullRequestSummary = jsonParser.decodeFromString(
            deserializer = PullRequestSummary.serializer(),
            string = JSON.stringify(pullRequestExample2WithoutAssigneeContents)
        )
        assertEquals("Making changes to the test file", pullRequestWithoutAssigneeSummary.title)
        assertEquals(0, pullRequestWithoutAssigneeSummary.assignees.size)
    }

    @Test
    fun shouldDeserialisePullRequestDetailsString() {
        val pullRequestDetails: PullRequestDetails = jsonParser.decodeFromString(PullRequestDetails.serializer(), JSON.stringify(pullRequestDetailsContents))
        assertEquals("Amazing new feature", pullRequestDetails.title)
        assertEquals(2, pullRequestDetails.assignees.size)
    }

    @Test
    fun shouldDeserialisePullRequestCommitListString() {
        val pullRequestCommit = jsonParser.decodeFromString(GithubCommit.serializer(), JSON.stringify(pullRequestCommitListEntry))
        assertEquals("Fix all the bugs", pullRequestCommit.commit.message)
        assertEquals(1, pullRequestCommit.parents.size)
    }

    @Test
    fun shouldDeserialisePullRequestFileListString() {
        val pullRequestFileDetails = jsonParser.decodeFromString(PullRequestFileDetails.serializer(), JSON.stringify(pullRequestFileListEntry))
        assertEquals("file1.txt", pullRequestFileDetails.filename)
        assertEquals(124L, pullRequestFileDetails.changes)
    }

    @Test
    fun shouldDeserialiseIssueComment() {
        val comment = jsonParser.decodeFromString(IssueComment.serializer(), JSON.stringify(issueComment))
        assertEquals("Me too", comment.body)
        assertEquals("octocat", comment.user.login)
    }

    @Test
    fun shouldDeserializeReview() {
        val review = jsonParser.decodeFromString(Review.serializer(), JSON.stringify(review))
        assertEquals("Here is the body for the review.", review.body)
        assertEquals("octocat", review.user.login)
    }

    @Test
    fun shouldDeserializeReviewComment() {
        val comment = jsonParser.decodeFromString(ReviewComment.serializer(), JSON.stringify(reviewComment))
        assertEquals("Great stuff!", comment.body)
        assertEquals("octocat", comment.user.login)
    }

    @Test
    fun shouldDeserializePullRequestComment() {
        val comment = jsonParser.decodeFromString(PullRequestReviewComment.serializer(), JSON.stringify(pullRequestComment))
        assertEquals("Dummy Comment", comment.body)
        assertEquals("yogeshnachnani", comment.user.login)
        assertEquals(7, comment.line)
    }
}