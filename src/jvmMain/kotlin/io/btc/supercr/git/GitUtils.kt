package io.btc.supercr.git

import codereview.DiffChangeType
import codereview.Edit
import codereview.FileDiff
import codereview.FileDiffList
import codereview.FileHeader
import io.btc.supercr.git.GitUtils.Companion.gitUtilsLogger
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset

class GitRepoNotFoundException(localPath: String): RuntimeException("Could not open git repo at $localPath")

class GitUtils {
    companion object {
        val gitUtilsLogger = LoggerFactory.getLogger(GitUtils::class.java)
    }
    fun openRepo(localPath: String, gitDirectory: String = ".git"): Git {
        return FileRepositoryBuilder()
            .setGitDir(File("$localPath/$gitDirectory"))
            .readEnvironment()
            .findGitDir()
            .build()
            .let { repo ->
                if (repo.branch != null) {
                    Git(repo)
                } else {
                    throw GitRepoNotFoundException(localPath)
                }
            }
    }
}

fun Git.fetchRef(ref: String): Boolean {
    return try {
        val fetchResult = this.fetch()
            .setRefSpecs(ref)
            .call()
        true
    } catch (e: Exception) {
        if (e.message?.contains("Remote does not have $ref") == true) {
            false
        } else {
            throw e
        }
    }
}

/**
 * TODO
 */
fun Git.checkOrFetchRef(ref: String): Boolean {
    return true
}

fun Git.formatDiff(oldRef: String, newRef: String): FileDiffList {
    val diffFormatter = DiffFormatter(System.out)
        .also {
            it.setRepository(this.repository)
        }
    return diffFormatter.scan(
        ObjectId.fromString(oldRef),
        ObjectId.fromString(newRef)
    )
        .map { diffEntry ->
            val diffFileHeader = diffFormatter.toFileHeader(diffEntry)
            FileDiff(
                rawTextOld = if (diffEntry.oldMode == FileMode.MISSING) {
                    null
                } else {
                    repository.fetchContents(  diffEntry.oldId.toObjectId())
                },
                rawTextNew = repository.fetchContents(diffEntry.newId.toObjectId()),
                diffChangeType = when(diffEntry.changeType) {
                    DiffEntry.ChangeType.ADD -> DiffChangeType.ADD
                    DiffEntry.ChangeType.MODIFY -> DiffChangeType.MODIFY
                    DiffEntry.ChangeType.DELETE -> DiffChangeType.DELETE
                    DiffEntry.ChangeType.RENAME -> DiffChangeType.RENAME
                    DiffEntry.ChangeType.COPY -> DiffChangeType.COPY
                    else -> throw RuntimeException("Not possible to have a null change type for file ${diffEntry.newPath}")
                },
                fileHeader = FileHeader(
                    description = String(diffFileHeader.buffer),
                    editList = diffFileHeader.toEditList()
                        .map { edit ->
                            Edit(
                                beginA = edit.beginA.toLong(),
                                beginB = edit.beginB.toLong(),
                                endA = edit.endA.toLong(),
                                endB = edit.endB.toLong()
                            )
                        }
                )
            )
        }
        .let {
            FileDiffList(it)
        }
}

private fun Repository.fetchContents(objectId: ObjectId): String {
    return ByteArrayOutputStream()
        .let {
            val newIdLoader = this.open(objectId)
            newIdLoader.copyTo(it)
            val toReturn = it.toString(Charset.defaultCharset())
            it.close()
            toReturn
        }
}
