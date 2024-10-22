package codereview

import git.provider.PullRequestReviewComment
import git.provider.ReviewCommentSide

data class FileDiffCommentHandler(
    var oldFileCommentHandler: FileCommentHandler?,
    var newFileCommentHandler: FileCommentHandler?
)

class FileCommentHandler(
    val existingComments: Map<Int, List<FileLineItem.Comment>>
) {
    private val newCommentsMutable: MutableMap<Int, List<FileLineItem.Comment>> = mutableMapOf()

    val addNewComment: (String, Int, String) -> Unit = { commentBody, position, createdAt ->
        val newComment = FileLineItem.Comment(
            body = commentBody,
            createdAt = createdAt,
            updatedAt = createdAt,
            userId = "yogeshnachnani"
        )
        /** We add entries to the [newCommentsMutable] as well as [allComments] maps */
        val existingNewCommentsOnGivenPosition = this.newCommentsMutable[position] ?: listOf()
        this.newCommentsMutable[position] = existingNewCommentsOnGivenPosition.plus(newComment)
    }

    fun hasNewComments(): Boolean {
        return this.newCommentsMutable.isNotEmpty()
    }

    val getNewCommentAt: (Int) -> List<FileLineItem.Comment> = { position ->
        this.newCommentsMutable[position] ?: listOf()
    }

    val newComments
    get() = this.newCommentsMutable.toMap()

}

fun List<Pair<FileDiffV2, FileDiffCommentHandler>>.retrieveChangedFileDiffList(): FileDiffListV2 {
    return this.mapNotNull { (oldFileDiff, commentHandler) ->
        if (commentHandler.hasNewComments()) {
            val (oldFileComments, newFileComments) = commentHandler.retrieveNewComments()
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

fun FileDiffCommentHandler.retrieveNewComments(): Pair<Map<Int, List<FileLineItem.Comment>>, Map<Int, List<FileLineItem.Comment>>> {
    return Pair(
        first = oldFileCommentHandler?.newComments ?: mapOf(),
        second = newFileCommentHandler?.newComments ?: mapOf()
    )
}

fun FileDiffCommentHandler.hasNewComments(): Boolean  {
    return oldFileCommentHandler?.hasNewComments() ?: false || newFileCommentHandler?.hasNewComments() ?: false
}

fun createCommentHandlerForFile(
    oldFile: FileData?,
    newFile: FileData?
): FileDiffCommentHandler {
    return FileDiffCommentHandler(
        oldFileCommentHandler = if (oldFile != null) {
            FileCommentHandler(existingComments = oldFile.retrieveViewPositionToCommentMap())
        } else {
            null
        },
        newFileCommentHandler = if (newFile != null) {
            FileCommentHandler(existingComments = newFile.retrieveViewPositionToCommentMap())
        } else {
            null
        }
    )
}

