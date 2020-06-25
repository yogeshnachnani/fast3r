package io.btc.supercr.db

data class FileReviewInfo(
    val path: String,
    val projectIdentifier: String,
    val pullRequestNumber: Long,
    val commitSha: String
) {
    val id
        get() = "${projectIdentifier}_${path.replace("/", "")}"
}

data class FileLineComment(
    val fileReviewId: String,
    val rowNumber: Long,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
    val userId: String
)