package io.btc.utils

import APP_NAME
import ch.qos.logback.core.util.FileUtil
import io.btc.supercr.api.initDb
import io.btc.supercr.db.FileLineComment
import io.btc.supercr.db.FileReviewInfo
import org.eclipse.jgit.util.FileUtils
import org.jdbi.v3.core.Jdbi
import java.io.File

class TestUtils {
    companion object {
        val btcRepoDir: String =  TestUtils::class.java.classLoader.getResource("placeholder")!!.file
            .let {
                /** We are in : src/jvmTest/resources/placeholder */
                File(it).parentFile.parentFile.parentFile.parentFile.parentFile.absolutePath
            }
        const val validBtcRef = "ae6adbf9f142c8591e2128484c87c4e50cdc19e7"

        val samplePullRequestSummaryJson =
            TestUtils::class.java.classLoader.getResourceAsStream("pull_request_example1.json")
                .let {
                    String(it!!.readAllBytes())
                }
    }
}

fun initTestDb(): Jdbi {
    return initDb(dbName = "test")
}

fun Jdbi.clearTestDb() {
    this.useTransaction<RuntimeException> { handle ->
        handle.createUpdate("""
                DELETE FROM project
            """.trimIndent())
            .execute()
        handle.createUpdate("""
                DELETE FROM file_line_comments
            """.trimIndent())
            .execute()
        handle.createUpdate("""
                DELETE FROM file_review_info
            """.trimIndent())
            .execute()
        handle.createUpdate("""
                DELETE FROM review_info
            """.trimIndent())
            .execute()
    }
}
fun getTestComment(fileReviewInfo: FileReviewInfo, commentBody: String, rowNumber: Long): FileLineComment {
    val createdAt = getCurrentTimeInIsoDateTime()
    return FileLineComment(
        fileReviewId = fileReviewInfo.rowId,
        rowNumber = rowNumber,
        body = commentBody,
        createdAt = createdAt,
        updatedAt = createdAt,
        userId = "foobar"
    )
}
