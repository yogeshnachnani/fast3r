package io.btc.supercr.db

import codereview.FileType
import codereview.ReviewInfo
import codereview.ReviewStorageProvider
import io.btc.utils.clearTestDb
import io.btc.utils.getTestComment
import io.btc.utils.initTestDb
import org.jdbi.v3.core.Jdbi
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FileLineItemsRepositoryTest {
    private lateinit var jdbi: Jdbi
    private lateinit var fileLineItemsRepository: FileLineItemsRepository

    @Before
    fun setUp() {
        if (! this::jdbi.isInitialized) {
            jdbi = initTestDb()
        }
        jdbi.clearTestDb()
        fileLineItemsRepository = FileLineItemsRepository(jdbi)
    }

    @Test
    fun testCreateReview() {
        val reviewToBeSaved = ReviewInfo(null, "someid", ReviewStorageProvider.GITHUB, 1L)
        val createdReview = fileLineItemsRepository.getOrCreateReview(reviewToBeSaved)
        assertEquals(reviewToBeSaved.projectIdentifier, createdReview.projectIdentifier)
        assertNotNull(createdReview.rowId)
    }
    
    @Test
    fun testRetrieveReviewById() {
        val createdReview = fileLineItemsRepository.getOrCreateReview(ReviewInfo(null, "someid", ReviewStorageProvider.GITHUB, 1L))
        val retrievedReview = fileLineItemsRepository.retrieveReview(createdReview.rowId!!)
        assertEquals(createdReview, retrievedReview)
    }

    @Test
    fun testCreateReview_ShouldGracefullyHandleDuplicates() {
        val createdReview = fileLineItemsRepository.getOrCreateReview(ReviewInfo(null, "someid", ReviewStorageProvider.GITHUB, 1L))
        val createdRowId = createdReview.rowId!!
        /** Will try to create another review of the same params */
        val secondAttemptReview = fileLineItemsRepository.getOrCreateReview(ReviewInfo(null, "someid", ReviewStorageProvider.GITHUB, 1L))
        assertEquals(createdRowId, secondAttemptReview.rowId)
    }

    @Test
    fun testCommentInsertionAndRetrieval() {
        val testFile1 = FileReviewInfo(null, "src/foobar", 1L, FileType.OLD_FILE)
        val testComment0File1 = getTestComment(testFile1, "This is comment 1", 0)
        val testComment1File1 = getTestComment(testFile1, "This is comment 2", 1)

        val testFile2 = FileReviewInfo(null,"src/foobar", 1L, FileType.NEW_FILE)
        val testComment0File2 = getTestComment(testFile2, "This is comment 11", 3)
        val testComment1File2 = getTestComment(testFile2, "This is comment 12", 4)

        val reviewAndComments = mapOf(
            testFile1 to listOf(testComment0File1, testComment1File1),
            testFile2 to listOf(testComment0File2, testComment1File2)
        )
        fileLineItemsRepository.addComments(reviewAndComments)

        fileLineItemsRepository.retrieveCommentsForReviewId(1L)
            .also { results ->
                results.forEach {(fileReviewInfo, comments) ->
                    assertNotNull(fileReviewInfo.rowId)
                    assertTrue(comments.all { it.fileReviewId == fileReviewInfo.rowId })
                }
                assertEquals(reviewAndComments.keys.size, results.keys.size)
                assertEquals(reviewAndComments.values.flatten().size, results.values.flatten().size)
            }
    }

    @Test
    fun testCommentInsertionAndRetrieval_ShouldGracefullyHandleExistingFileReviewInfo() {
        val testFile1 = FileReviewInfo(null, "src/foobar", 1L, FileType.OLD_FILE)
        val testComment0File1 = getTestComment(testFile1, "This is comment 1", 0)
        val testComment1File1 = getTestComment(testFile1, "This is comment 2", 1)

        val testFile2 = FileReviewInfo(null,"src/foobar", 1L, FileType.NEW_FILE)
        val testComment0File2 = getTestComment(testFile2, "This is comment 11", 3)
        val testComment1File2 = getTestComment(testFile2, "This is comment 12", 4)

        /** Save info for [testFile1] */
        fileLineItemsRepository.addComments(mapOf(testFile1 to listOf(testComment0File1, testComment1File1)))

        val reviewAndComments = mapOf(
            testFile1 to listOf(testComment0File1, testComment1File1),
            testFile2 to listOf(testComment0File2, testComment1File2)
        )
        val savedFileInfo = fileLineItemsRepository.retrieveCommentsForReviewId(1L)

        /** Should gracefully handle this call */
        fileLineItemsRepository.addComments(reviewAndComments)

        fileLineItemsRepository.retrieveCommentsForReviewId(1L)
            .also { results ->
                val fileReviewInfoAndCommentsForAlreadySavedFile = results[savedFileInfo.keys.first()]
                assertNotNull(fileReviewInfoAndCommentsForAlreadySavedFile)
                /** Note: There is no uniqueness on comments, so we'll have 4 comments for this file now */
                assertEquals(4, fileReviewInfoAndCommentsForAlreadySavedFile.size)
            }
    }

    @Test
    fun shouldReturnGracefullyIfNoCommentsFound() {
        fileLineItemsRepository.retrieveCommentsForReviewId(1L)
            .also { results ->
                assertTrue(results.isEmpty())
            }
    }

}