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
        .setGitDir(File("/home/yogesh/work/theboringtech.github.io/.git"))
        .readEnvironment()
        .findGitDir()
        .build()
        .let { repo ->
            DiffFormatter(System.out)
                .also {
                    it.setRepository(repo)
                }
                .let { diffFormatter ->
                    diffFormatter.scan(
                        ObjectId.fromString("168242420aa58e2c65921adca573df703e01292f"),
                        ObjectId.fromString("c5549e620254c3b0a56b3a8b37dea197ba0e9236")
                    )
                        .forEach { diffEntry ->
                            println("Processing diff entry $diffEntry")
                            println("Got text ${diffFormatter.toFileHeader(diffEntry).scriptText}")
                            println("Got edit List ${diffFormatter.toFileHeader(diffEntry).toEditList()}")
                        }
                    println("**** Now checking changes ***")
                    diffFormatter.format(
                        ObjectId.fromString("168242420aa58e2c65921adca573df703e01292f"),
                        ObjectId.fromString("c5549e620254c3b0a56b3a8b37dea197ba0e9236")
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