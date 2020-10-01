package io.btc.supercr.git.processor

import codereview.Edit
import codereview.FileLineItem
import codereview.SimpleFileDiff
import git.provider.GithubAuthorAssociation
import git.provider.GithubLink
import git.provider.GithubLinkRelations
import git.provider.PullRequestReviewComment
import git.provider.ReviewCommentSide
import git.provider.User
import jsonParser
import org.apache.commons.io.FileUtils
import org.junit.Test
import java.io.File
import java.nio.charset.Charset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiffProcessorTest {

    val rawTextOld = """
                public class FooBarBaz {
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd line");
                    System.out.println("This is 3rd line");
                    System.out.println("This is 4th line");
                  }
                }
            
        """.trimIndent()
    val rawTextNew = """
                public class FooBarBaz {
                  // Adding a comment - which will be a completely new line
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd changed line");
                    System.out.println("This is 3rd changed line");
                    System.out.println("This is a new line");
                    System.out.println("This is 4th line");

                }
        """.trimIndent()

    val editList = listOf(
        Edit(beginA = 1, endA = 1, beginB = 1, endB = 2),
        Edit(beginA = 3, endA = 5, beginB = 4, endB = 7),
        Edit(beginA = 6, endA = 8, beginB = 8, endB = 8),
        Edit(beginA = 9, endA = 9, beginB = 9, endB = 10)
    )

    @Test
    fun processEdits_ShouldReturnEmptyIfOldTextIsNulL() {
        val (oldFileLines, newFileLines) = listOf<Edit>().processWith(null, rawTextNew)
        assertTrue(oldFileLines.isEmpty())
        assertEquals(10, newFileLines.size)
    }

    @Test
    fun processEdits_ShouldReturnEmptyIfNewTextIsNulL() {
        val (oldFileLines, newFileLines) = listOf<Edit>().processWith(rawTextOld, null)
        assertTrue(newFileLines.isEmpty())
        assertEquals(9, oldFileLines.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun processEdits_ShouldBombIfBothTextIsEmpty() {
         listOf<Edit>().processWith(null, null)
    }

    @Test
    fun processEdits_ShouldAddLinesInOldTextToBalance() {
        val (oldFileLines, newFileLines) = editList.processWith(rawTextOld, rawTextNew)
        assertTrue(oldFileLines.size == newFileLines.size)
        assertEquals(12, oldFileLines.size)
        assertEquals(3 ,oldFileLines.filter { it.filePosition == null }.size)
    }

    @Test
    fun processEdits_ShouldAddLinesInNewTextToBalance() {
        val (oldFileLines, newFileLines) = editList.processWith(rawTextOld, rawTextNew)
        assertTrue(oldFileLines.size == newFileLines.size)
        assertEquals(12, newFileLines.size)
        assertEquals(2 ,newFileLines.filter { it.filePosition == null }.size)
    }

    @Test
    fun processEdits_ShouldAddLinesInNewTextWhenReplaceLengthBIsLessThanLengthA() {
        val simpleFileDiff = DiffProcessorTest::class.java.classLoader
            .getResource("textDiffWithEditList_ReplaceEditWithLengthBLesserThanA.json")!!
            .file
            .let {
                jsonParser.decodeFromString(SimpleFileDiff.serializer(), FileUtils.readFileToString(File(it), Charset.defaultCharset()))
            }
        val (oldFileLines, newFileLines) = simpleFileDiff.editList.processWith(simpleFileDiff.oldFileText, simpleFileDiff.newFileText)
        assertEquals(254, oldFileLines.size)
        assertEquals(oldFileLines.size, newFileLines.size)
    }

    @Test
    fun processComments_ShouldAddCommentsBasedOnOriginalLineNumber() {
        val existingCommentsForFile = listOf(
            createDummyComment( 4, 6, "Some comment", ReviewCommentSide.LEFT),
            createDummyComment( 4, 6, "Another comment on same line", ReviewCommentSide.LEFT),
            createDummyComment( 6, 8, "Comment on some other line", ReviewCommentSide.RIGHT),
            createDummyComment( 7, 9, "Comment on right line 9", ReviewCommentSide.RIGHT)
        )

        val expectedCommentsOnLine6 = listOf(
            FileLineItem.Comment("Some comment", DUMMY_CREATED_AT, DUMMY_CREATED_AT, dummyUser.login),
            FileLineItem.Comment("Another comment on same line", DUMMY_CREATED_AT, DUMMY_CREATED_AT, dummyUser.login)
        )
        val expectedCommentOnLine8 = listOf(
            FileLineItem.Comment("Comment on some other line", DUMMY_CREATED_AT, DUMMY_CREATED_AT, dummyUser.login),
        )
        val expectedCommentOnLine9 = listOf(
            FileLineItem.Comment("Comment on right line 9", DUMMY_CREATED_AT, DUMMY_CREATED_AT, dummyUser.login),
        )

        val (oldFileLines, newFileLines) = listOf<Edit>().processWith(rawTextOld, rawTextNew, existingCommentsForFile)
        /** Note: Github starts it's position from 1 while we start file position from 0. Hence, when a Github comment says it's on line 6, we want it to be on filePosition = 5 */
        /** Check for old file comments */
        assertEquals(1, oldFileLines.filter { it.lineItems.isNotEmpty() }.size)
        assertEquals(expectedCommentsOnLine6, oldFileLines.find { it.filePosition == 5 }!!.lineItems)
        /** Check for new file comments */
        assertEquals(2, newFileLines.filter { it.lineItems.isNotEmpty() }.size)
        assertEquals(expectedCommentOnLine8, newFileLines.find { it.filePosition == 7 }!!.lineItems)
        assertEquals(expectedCommentOnLine9, newFileLines.find { it.filePosition == 8 }!!.lineItems)
    }

    private fun createDummyComment(
        position: Long,
        line: Long,
        commentBody: String,
        side: ReviewCommentSide
    ): PullRequestReviewComment {
        return PullRequestReviewComment(
            url = "https://api.github.com/something/foo/bar",
            pull_request_review_id = 112233,
            id = 112244,
            node_id = "998112",
            diff_hunk = "",
            path = "/home/foo/bar.kt",
            position = position,
            original_position = position,
            commit_id = "some-commit",
            original_commit_id = "some-commit",
            user = dummyUser,
            body = commentBody,
            created_at = DUMMY_CREATED_AT,
            updated_at = DUMMY_CREATED_AT,
            html_url = "",
            pull_request_url = "",
            author_association = GithubAuthorAssociation.CONTRIBUTOR,
            _links = GithubLinkRelations(
                html = GithubLink(href = "")
            ),
            side = side,
            line = line,
            original_line = line
        )
    }

    private val dummyUser = User(
        login = "dummyuser",
        id = 121212,
        node_id = "121212",
        avatar_url = "http://some.avatar",
        gravatar_id = "http://some.gravatar",
        url = "http://api.github.com/someuser",
        html_url = "http://api.github.com/someuser",
        followers_url = "",
        following_url = "",
        gists_url = "",
        starred_url = "",
        subscriptions_url = "",
        organizations_url = "",
        repos_url = "",
        events_url = "",
        received_events_url = "",
        type = "",
        site_admin = false
    )
    private val DUMMY_CREATED_AT = "2020-09-28 00:00:00"
}