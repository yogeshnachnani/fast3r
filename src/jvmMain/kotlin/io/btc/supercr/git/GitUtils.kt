package io.btc.supercr.git

import codereview.FileDiffListV2
import git.provider.PullRequestReviewComment
import io.btc.supercr.git.processor.process
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.errors.MissingObjectException
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception

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
 * TODO: This will take a long time right now. Try to speed this up somehow
 */
fun Git.checkOrFetchRef(ref: String): Boolean {
    try {
        repository.parseCommit(ObjectId.fromString(ref))
    } catch (exception: MissingObjectException) {
        this.fetchRef(ref)
    }
    return true
}

fun Git.formatDiffV2(oldRef: String, newRef: String, existingComments: List<PullRequestReviewComment> = emptyList()): FileDiffListV2 {
    val diffFormatter = DiffFormatter(System.out)
        .also {
            it.setRepository(this.repository)
            it.isDetectRenames = true
        }
    return diffFormatter.scan(
        ObjectId.fromString(oldRef),
        ObjectId.fromString(newRef)
    )
        .map { diffEntry ->
            val oldFileText = if (diffEntry.oldMode == FileMode.MISSING) {
                null
            } else {
                repository.fetchContents(  diffEntry.oldId.toObjectId())
            }
            val newFileText = if (diffEntry.newMode == FileMode.MISSING) {
                null
            } else {
                repository.fetchContents(diffEntry.newId.toObjectId())
            }
            diffFormatter.process(diffEntry = diffEntry, oldFileText = oldFileText, newFileText = newFileText, existingComments)
        }
        .let {
            FileDiffListV2(it)
        }
}

private fun Repository.fetchContents(objectId: ObjectId): String {
    return ByteArrayOutputStream()
        .let {
            val newIdLoader = this.open(objectId)
            newIdLoader.copyTo(it)
            val toReturn = it.toString()
            it.close()
            toReturn
        }
}

