package io.btc.supercr.db

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindList
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.attach
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface FileLineItemsDao {
    @SqlBatch("""
        INSERT INTO file_line_comments (fileReviewId, rowNumber, body, createdAt, updatedAt, userId)
        VALUES(:fileReviewId, :rowNumber,:body,:createdAt,:updatedAt,:userId)
    """)
    fun insertComment(@BindKotlin comments: List<FileLineComment>): IntArray

    @SqlBatch("""
        INSERT INTO file_review_info (id, path, projectIdentifier, pullRequestNumber, commitSha) 
        VALUES (:id, :path, :projectIdentifier, :pullRequestNumber, :commitSha) 
    """)
    fun insertFileReviewInfo(@BindKotlin reviewInfo: List<FileReviewInfo>): IntArray

    @SqlQuery("""
        SELECT * FROM file_line_comments
        WHERE fileReviewId = :file_id
    """)
    fun retrieveCommentsForFile(@Bind("file_id") fileReviewId: String): List<FileLineComment>

    @SqlQuery("""
        SELECT * FROM file_line_comments
        WHERE fileReviewId in (<file_ids>)
    """)
    fun retrieveCommentsForFiles(@BindList("file_ids") fileReviewId: List<String>): List<FileLineComment>

    @SqlQuery("""
        SELECT * from file_review_info
        WHERE projectIdentifier = :project_id and pullRequestNumber = :pull_request_number
    """)
    fun retrieveFilesForReview(
        @Bind("pull_request_number") pullRequestNumber: Long,
        @Bind("project_id") projectIdentfier: String
    ): List<FileReviewInfo>
}

class FileLineItemsRepository constructor(
    private val jdbi: Jdbi
){

    fun addComments(fileInfoAndComments: Map<FileReviewInfo, List<FileLineComment>>) {
        /** Verify input */
        require(
            fileInfoAndComments.all { (fileReviewInfo, comments) ->
                comments.all { it.fileReviewId == fileReviewInfo.id }
            }
        )
        jdbi.useTransaction<RuntimeException> { handle ->
            val fileLineItemsDao: FileLineItemsDao = handle.attach()
            fileLineItemsDao.insertFileReviewInfo(fileInfoAndComments.keys.toList())
                .also {
                    require(it.all { value -> value == 1 })
                }
            fileLineItemsDao.insertComment(fileInfoAndComments.values.flatten())
                .also {
                    require(it.all { value -> value == 1 })
                }
        }
    }

    fun retrieveCommentsForPullRequest(pullRequestNumber: Long, projectIdentfier: String): Map<FileReviewInfo, List<FileLineComment>> {
        return jdbi.withHandle<Map<FileReviewInfo, List<FileLineComment>>, RuntimeException> { handle ->
            val fileLineItemsDao: FileLineItemsDao = handle.attach()
            fileLineItemsDao.retrieveFilesForReview(pullRequestNumber, projectIdentfier)
                .let { fileReviewInfos ->
                    val comments = fileLineItemsDao.retrieveCommentsForFiles(fileReviewInfos.map { it.id })
                    comments.groupBy { comment ->
                        fileReviewInfos.find { it.id == comment.fileReviewId }!!
                    }
                }
        }
    }
}
