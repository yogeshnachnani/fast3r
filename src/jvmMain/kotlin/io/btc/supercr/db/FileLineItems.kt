package io.btc.supercr.db

import codereview.FileLineItem
import io.ktor.util.sha1
import java.nio.charset.Charset

enum class FileType{
    OLD_FILE,
    NEW_FILE
}

data class FileReviewInfo(
    val path: String,
    val projectIdentifier: String,
    val pullRequestNumber: Long,
    val fileType: FileType
) {
    /** TODO: This is horrible. Needs to change */
    val id
        get() = "${projectIdentifier}_${path.replace("/", "")}_$fileType"
}

data class FileLineComment(
    val fileReviewId: String,
    val rowNumber: Long,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
    val userId: String
)

fun FileLineComment.toLineComment(): FileLineItem.LineComment {
    return FileLineItem.LineComment(body, createdAt, updatedAt, userId)
}