package io.btc.supercr.db

import io.btc.utils.clearTestDb
import io.btc.utils.getCurrentTimeInIsoDateTime
import io.btc.utils.initTestDb
import org.jdbi.v3.core.Jdbi
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class FileLineItemsRepositoryTest {
    private lateinit var jdbi: Jdbi
    private lateinit var fileLineItemsRepository: FileLineItemsRepository
    private val PULL_REQUEST_NUMBER = 1L
    private val PROJECT_ID = "someproject"

    @Before
    fun setUp() {
        if (! this::jdbi.isInitialized) {
            jdbi = initTestDb()
        }
        jdbi.clearTestDb()
        fileLineItemsRepository = FileLineItemsRepository(jdbi)
    }
    val testFile1 = FileReviewInfo("src/foobar", PROJECT_ID, PULL_REQUEST_NUMBER, "abcdefgh")
    val testComment0File1 = getTestComment(testFile1, "This is comment 1", 0)
    val testComment1File1 = getTestComment(testFile1, "This is comment 2", 2)


    @Test
    fun testCommentInsertionAndRetrieval() {
        val reviewAndComments = mapOf(
            testFile1 to listOf(testComment0File1, testComment1File1)
        )
        fileLineItemsRepository.addComments(reviewAndComments)

        fileLineItemsRepository.retrieveCommentsForPullRequest(PULL_REQUEST_NUMBER, PROJECT_ID)
            .also { results ->
                assertEquals(reviewAndComments, results)
            }
    }

    private fun getTestComment(fileReviewInfo: FileReviewInfo, commentBody: String, rowNumber: Long): FileLineComment {
        val createdAt = getCurrentTimeInIsoDateTime()
        return FileLineComment(
            fileReviewId = fileReviewInfo.id,
            rowNumber = rowNumber,
            body = commentBody,
            createdAt = createdAt,
            updatedAt = createdAt,
            userId = "foobar"
        )
    }
}