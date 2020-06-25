package io.btc.supercr.review

import codereview.FileData
import codereview.FileDiffListV2
import codereview.Project
import io.btc.supercr.db.FileLineComment
import io.btc.supercr.db.FileLineItemsRepository
import io.btc.supercr.db.FileType
import io.btc.supercr.db.toLineComment

class ReviewController constructor(
    private val fileLineItemsRepository: FileLineItemsRepository
) {
    fun retrieveCommentsFor(fileDiffListV2: FileDiffListV2, project: Project, reviewId: Long): FileDiffListV2 {
        return fileLineItemsRepository.retrieveCommentsForReviewId(reviewId, project.id)
            .let { fileReviewInfoAndComments  ->
                fileDiffListV2.fileDiffs.map { fileDiff ->
                    val commentsForOldFile = if(fileDiff.oldFile != null) {
                        fileReviewInfoAndComments
                            .filterKeys { fileReviewInfo -> fileDiff.oldFile.path == fileReviewInfo.path && fileReviewInfo.fileType == FileType.OLD_FILE}
                            .values
                            .flatten()
                            .groupBy {
                                it.rowNumber
                            }
                    } else {
                        mapOf()
                    }
                    val commentsForNewFile = if(fileDiff.newFile != null) {
                        fileReviewInfoAndComments
                            .filterKeys { fileReviewInfo -> fileDiff.newFile.path == fileReviewInfo.path && fileReviewInfo.fileType == FileType.NEW_FILE }
                            .values
                            .flatten()
                            .groupBy {
                                it.rowNumber
                            }
                    } else {
                        mapOf()
                    }
                    fileDiff.copy(
                        oldFile = commentsForOldFile.appendCommentDataTo(fileDiff.oldFile),
                        newFile = commentsForNewFile.appendCommentDataTo(fileDiff.newFile)
                    )
                }
            }
            .let {
                FileDiffListV2(it)
            }
    }

    private fun Map<Long, List<FileLineComment>>.appendCommentDataTo(
        fileData: FileData?
    ): FileData? {
        return if (this.isNotEmpty()) {
            fileData!!.fileLines
                .map { fileLine ->
                    val foo = this[fileLine.filePosition?.toLong() ?: -1]
                        ?.map {
                            it.toLineComment()
                        }
                    if (foo != null) {
                        fileLine.lineItems.plus(foo)
                            .let {
                                fileLine.copy(lineItems = it)
                            }
                    } else {
                        fileLine
                    }
                }.let {
                fileData.copy(fileLines = it)
            }
        } else {
            fileData
        }
    }
}