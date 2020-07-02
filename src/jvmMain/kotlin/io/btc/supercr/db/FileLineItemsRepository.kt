package io.btc.supercr.db

import codereview.ReviewInfo
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindList
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.attach
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface FileLineItemsDao {

    @SqlQuery("""
        SELECT rowid, projectIdentifier, provider, providerId
        FROM review_info
        WHERE projectIdentifier = :project_identifier
        AND provider = :provider
        AND providerId = :provider_id
    """)
    fun retrieveReviewFor(
        @Bind("project_identifier") projectIdentifier: String,
        @Bind("provider") provider: String,
        @Bind("provider_id") providerId: Long
    ): ReviewInfo?

    @SqlQuery("""
        SELECT rowid, projectIdentifier, provider, providerId
        FROM review_info
        WHERE rowid = :review_id
    """)
    fun retrieveReviewInfo(@Bind("review_id") reviewId: Long) : ReviewInfo?

    @SqlUpdate("""
        INSERT INTO review_info(projectIdentifier, provider, providerId)
        VALUES (:project_identifier, :provider, :provider_id)
    """
    )
    fun createReviewInfo(
        @Bind("project_identifier") projectIdentifier: String,
        @Bind("provider") provider: String,
        @Bind("provider_id") providerId: Long
    ): Int

    @SqlBatch("""
        INSERT INTO file_line_comments (fileReviewId, rowNumber, body, createdAt, updatedAt, userId)
        VALUES(:fileReviewId, :rowNumber,:body,:createdAt,:updatedAt,:userId)
    """)
    fun insertComment(@BindKotlin comments: List<FileLineComment>): IntArray

    @SqlUpdate("""
        INSERT INTO file_review_info (path, reviewId, fileType) 
        VALUES (:path, :reviewId, :fileType) 
    """)
    @GetGeneratedKeys("rowid")
    fun insertFileReviewInfo(@BindKotlin reviewInfo: FileReviewInfo): Long


    @SqlQuery("""
        SELECT * FROM file_line_comments
        WHERE fileReviewId = :file_id
    """)
    fun retrieveCommentsForFile(@Bind("file_id") fileReviewId: String): List<FileLineComment>

    @SqlQuery("""
        SELECT * FROM file_line_comments
        WHERE fileReviewId in (<file_ids>)
    """)
    fun retrieveCommentsForFiles(@BindList("file_ids") fileReviewId: List<Long>): List<FileLineComment>

    @SqlQuery("""
        SELECT rowid, path, reviewId, fileType from file_review_info
        WHERE reviewId = :review_id
    """)
    fun retrieveFilesForReview(
        @Bind("review_id") reviewId: Long
    ): List<FileReviewInfo>

    @SqlQuery("""
        SELECT rowid, path, reviewId, fileType from file_review_info
        WHERE path = :path
        AND reviewId = :review_id
        AND fileType = :file_type
    """)
    fun retrieveFileFor(@Bind("path") path: String, @Bind("review_id") reviewId: Long, @Bind("file_type") fileType: String): FileReviewInfo?

}

class FileLineItemsRepository constructor(
    private val jdbi: Jdbi
){

    fun getOrCreateReview(reviewInfo: ReviewInfo): ReviewInfo {
        return jdbi.inTransaction<ReviewInfo, RuntimeException> { handle ->
            val fileLineItemsDao: FileLineItemsDao = handle.attach()
            with(reviewInfo) {
                try {
                    fileLineItemsDao.createReviewInfo(projectIdentifier = projectIdentifier, provider = provider.name, providerId = providerId)
                } catch (e: Exception) {
                    if (e.message?.contains("SQLITE_CONSTRAINT") == true) {
                        fileLineItemsDao.retrieveReviewFor(projectIdentifier = projectIdentifier, provider = provider.name, providerId = providerId)
                    } else{
                        throw e
                    }
                }
                fileLineItemsDao.retrieveReviewFor(projectIdentifier = projectIdentifier, provider = provider.name, providerId = providerId)
            }
        }
    }

    fun addComments(fileInfoAndComments: Map<FileReviewInfo, List<FileLineComment>>) {
        /** Verify input */
        require(
            fileInfoAndComments.all { (fileReviewInfo, comments) ->
                comments.all { it.fileReviewId == fileReviewInfo.rowId }
            }
        )
        jdbi.useTransaction<RuntimeException> { handle ->
            val fileLineItemsDao: FileLineItemsDao = handle.attach()
            /** First insert [FileReviewInfo] */
            fileInfoAndComments
                .map { (fileReviewInfo, comments) ->
                    val rowIdForFileReviewInfo =try {
                        fileLineItemsDao.insertFileReviewInfo(fileReviewInfo)
                    } catch (e: Exception) {
                        if (e.message?.contains("SQLITE_CONSTRAINT") == true) {
                            /** [FileReviewInfo]  already present. Retrieve it so we get the rowid */
                            fileLineItemsDao.retrieveFileFor(fileReviewInfo.path, fileReviewInfo.reviewId, fileReviewInfo.fileType.name)!!.rowId!!
                        } else {
                            throw e
                        }
                    }
                    /** Populate the row id in comments */
                    comments.map { it.copy(fileReviewId = rowIdForFileReviewInfo) }
                }
                .flatten()
                .apply {
                    fileLineItemsDao.insertComment(this)
                        .also {
                            require(it.all { value -> value == 1 })
                        }
                }
        }
    }

    fun retrieveCommentsForReviewId(reviewId: Long): Map<FileReviewInfo, List<FileLineComment>> {
        return jdbi.withHandle<Map<FileReviewInfo, List<FileLineComment>>, RuntimeException> { handle ->
            val fileLineItemsDao: FileLineItemsDao = handle.attach()
            fileLineItemsDao.retrieveFilesForReview(reviewId)
                .let { fileReviewInfos ->
                    if (fileReviewInfos.isEmpty()) {
                        emptyMap()
                    } else {
                        val comments = fileLineItemsDao.retrieveCommentsForFiles(fileReviewInfos.map { it.rowId!! })
                        comments.groupBy { comment ->
                            fileReviewInfos.find { it.rowId == comment.fileReviewId }!!
                        }
                    }
                }
        }
    }

    fun retrieveReview(reviewId: Long): ReviewInfo? {
        return jdbi.withHandle<ReviewInfo?, RuntimeException> {handle ->
            val fileLineItemsDao: FileLineItemsDao = handle.attach()
            fileLineItemsDao.retrieveReviewInfo(reviewId)
        }
    }
}
