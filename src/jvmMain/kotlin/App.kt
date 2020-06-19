import io.btc.supercr.api.ApiServer
import io.btc.supercr.git.GitUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import java.io.File

fun main() {
    println("Hello from the world of kotlin")
//    tryoutGit()
//    tryFetchRef()
    ApiServer()
}

private fun tryoutGit() {
    FileRepositoryBuilder()
        .setGitDir(File("/home/yogesh/work/btc/.git"))
        .readEnvironment()
        .findGitDir()
        .build()
        .let { repo ->
            DiffFormatter(System.out)
                .also {
                    it.setRepository(repo)
                    it.isDetectRenames = true
                }
                .let { diffFormatter ->
                    diffFormatter.scan(
                        // old diff
                        ObjectId.fromString("d9883ad3e5a5b4d08faef91ac8471e60438077b6"),
                        ObjectId.fromString("c5873d613cb8c0e8c7aa8fd5599e6eda64d8a4a0")
                    )
                        .forEach { diffEntry ->
                            println("Processing diff entry $diffEntry")
                            println("Got text ${diffFormatter.toFileHeader(diffEntry).scriptText}")
                            println("Got edit List ${diffFormatter.toFileHeader(diffEntry).toEditList()}")
                        }
                    println("**** Now checking changes ***")
                    diffFormatter.format(
                        ObjectId.fromString("d9883ad3e5a5b4d08faef91ac8471e60438077b6"),
                        ObjectId.fromString("c5873d613cb8c0e8c7aa8fd5599e6eda64d8a4a0")
                    )
                }

        }
}

fun Repository.diffForPr(baseRef: String, prHead: String): List<DiffEntry> {
    val baseTree = this.getTree(baseRef)
    val prTree = this.getTree(prHead)
    return Git(this).diff().setOldTree(baseTree).setNewTree(prTree).call()
}

fun Repository.getTree(commitSha: String): AbstractTreeIterator {
    return RevWalk(this)
        .let {
            val commit = it.parseCommit(ObjectId.fromString(commitSha))
            val tree = it.parseTree(commit.tree.id)

            val treeParser = CanonicalTreeParser()
            this.newObjectReader()
                .also { reader ->
                    treeParser.reset(reader, tree.id)
                }
            it.dispose()
            treeParser
        }
}