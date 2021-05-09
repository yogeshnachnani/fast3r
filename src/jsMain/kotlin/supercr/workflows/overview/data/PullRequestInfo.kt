package supercr.workflows.overview.data

import codereview.FileDiffListV2
import codereview.Project
import git.provider.PullRequestSummary

/**
 * All Information related to a given Pull request clubbed into a single class for easy passing around
 */
data class PullRequestInfo(
    val project: Project,
    val pullRequestSummary: PullRequestSummary,
    val fileDiffListV2: FileDiffListV2,
    val keyboardShortcut: String
)
