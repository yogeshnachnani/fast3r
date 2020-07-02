package io.btc.supercr.review

import codereview.FileData
import codereview.FileDiffListV2
import codereview.FileLineItem
import codereview.FileType
import codereview.ReviewInfo
import codereview.retrieveAllLineItems
import io.btc.supercr.db.FileLineComment
import io.btc.supercr.db.FileLineItemsRepository
import io.btc.supercr.db.FileReviewInfo
import io.btc.supercr.db.toFileLineComment
import io.btc.supercr.db.toLineComment

class ReviewController constructor(
    private val fileLineItemsRepository: FileLineItemsRepository
) {
    fun storeCommentsFor(fileDiffListV2: FileDiffListV2, reviewId: Long) {
        fileDiffListV2.fileDiffs
            .flatMap { fileDiff ->
                val oldFileInfoAndComments = fileDiff.oldFile?.convertToFileInfoAndLineComments(reviewId, FileType.OLD_FILE)
                val newFileInfoAndComments = fileDiff.newFile?.convertToFileInfoAndLineComments(reviewId, FileType.NEW_FILE)
                listOfNotNull(oldFileInfoAndComments, newFileInfoAndComments)
            }
            .toMap()
            .apply {
                fileLineItemsRepository.addComments(this)
            }
    }

    fun retrieveCommentsFor(fileDiffListV2: FileDiffListV2, reviewId: Long): FileDiffListV2 {
        return fileLineItemsRepository.retrieveCommentsForReviewId(reviewId)
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

    fun getOrCreateReview(reviewInfo: ReviewInfo): ReviewInfo {
        return fileLineItemsRepository.getOrCreateReview(reviewInfo)
    }

    fun fetchReview(reviewId: Long): ReviewInfo? {
        return fileLineItemsRepository.retrieveReview(reviewId)
    }

    private fun FileData.convertToFileInfoAndLineComments(reviewId: Long, fileType: FileType): Pair<FileReviewInfo, List<FileLineComment>>? {
        val lineItems = this.retrieveAllLineItems()
        return if (lineItems.isNotEmpty()) {
            val fileReviewInfo = FileReviewInfo(null,  this.path, reviewId, fileType)
            val lineComments = lineItems.flatMap { (_, filePosition, comments) ->
                comments.map {
                    ( it  as FileLineItem.Comment ).toFileLineComment(filePosition!!.toLong())
                }
            }
            Pair(fileReviewInfo, lineComments)
        } else {
            null
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