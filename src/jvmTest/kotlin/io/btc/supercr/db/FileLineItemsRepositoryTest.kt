package io.btc.supercr.db

import io.btc.utils.clearTestDb
import io.btc.utils.getCurrentTimeInIsoDateTime
import io.btc.utils.getTestComment
import io.btc.utils.initTestDb
import org.jdbi.v3.core.Jdbi
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    val testFile1 = FileReviewInfo("src/foobar", PROJECT_ID, PULL_REQUEST_NUMBER, FileType.OLD_FILE)
    val testComment0File1 = getTestComment(testFile1, "This is comment 1", 0)
    val testComment1File1 = getTestComment(testFile1, "This is comment 2", 2)

    val testFile2 = FileReviewInfo("src/foobar", PROJECT_ID, PULL_REQUEST_NUMBER, FileType.NEW_FILE)
    val testComment0File2 = getTestComment(testFile2, "This is comment 1", 0)
    val testComment1File2 = getTestComment(testFile2, "This is comment 2", 2)


    @Test
    fun testCommentInsertionAndRetrieval() {
        val reviewAndComments = mapOf(
            testFile1 to listOf(testComment0File1, testComment1File1),
            testFile2 to listOf(testComment0File2, testComment1File2)
        )
        fileLineItemsRepository.addComments(reviewAndComments)

        fileLineItemsRepository.retrieveCommentsForReviewId(PULL_REQUEST_NUMBER, PROJECT_ID)
            .also { results ->
                assertEquals(reviewAndComments, results)
            }
    }

    @Test
    fun shouldReturnGracefullyIfNoCommentsFound() {
        fileLineItemsRepository.retrieveCommentsForReviewId(PULL_REQUEST_NUMBER, PROJECT_ID)
            .also { results ->
                assertTrue(results.isEmpty())
            }
    }

}