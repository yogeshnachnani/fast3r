package supercr.workflows.codereview.processor

import codereview.FileDiffListV2
import codereview.FileDiffV2
import codereview.FileLine
import codereview.FileLineItem
import kotlin.js.Date

data class FileDiffCommentHandler(
    var oldFileCommentHandler: FileCommentHandler?,
    var newFileCommentHandler: FileCommentHandler?
)

class FileCommentHandler(
    var comments: Map<Int, List<FileLineItem.Comment>>
) {
    val addNewComment: (String, Int) -> Unit = { commentBody, position ->
        val existingComments = this.comments[position] ?: listOf()
        val createdAt = Date().toISOString()
        val newComment = FileLineItem.Comment(
            body = commentBody,
            createdAt = createdAt,
            updatedAt = createdAt,
            userId = "yogeshnachnani"
        )
        this.comments = comments.plus(Pair(position, existingComments.plus(newComment)))
    }
}

fun List<Pair<FileDiffV2, FileDiffCommentHandler>>.retrieveChangedFileDiffList(): FileDiffListV2 {
    return this.mapNotNull { (oldFileDiff, commentHandler) ->
        if (commentHandler.hasNewComments()) {
            val (oldFileComments, newFileComments) = commentHandler.retrieveComments()
            with(oldFileDiff){
                copy(
                    oldFile = oldFile?.copy(fileLines = oldFile.fileLines.replaceWithNewComments(oldFileComments)),
                    newFile = newFile?.copy(fileLines = newFile.fileLines.replaceWithNewComments(newFileComments))
                )
            }
        } else {
            null
        }
    }
        .let {
            console.log("Found Following files with new comments: $it")
            FileDiffListV2(it)
        }
}

fun List<FileLine>.replaceWithNewComments(newCommentsMap: Map<Int, List<FileLineItem.Comment>>): List<FileLine> {
    return if (newCommentsMap.isNotEmpty()) {
        this.mapIndexed { index, fileLine ->
            val newCommentsForLine = newCommentsMap[index]
            if (newCommentsForLine != null) {
                fileLine.copy(
                    lineItems = newCommentsForLine
                )
            } else {
                fileLine
            }
        }
    } else {
        this
    }
}

fun FileDiffCommentHandler.retrieveComments(): Pair<Map<Int, List<FileLineItem.Comment>>, Map<Int, List<FileLineItem.Comment>>> {
    return Pair(
        first = oldFileCommentHandler?.comments ?: mapOf(),
        second = newFileCommentHandler?.comments ?: mapOf()
    )
}

fun FileDiffCommentHandler.hasNewComments(): Boolean  {
    return oldFileCommentHandler?.hasNewComments() ?: false || newFileCommentHandler?.hasNewComments() ?: false
}

fun FileCommentHandler.hasNewComments(): Boolean {
    return this.comments.isNotEmpty()
}
