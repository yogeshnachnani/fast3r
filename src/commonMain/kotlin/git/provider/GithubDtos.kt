package git.provider
import DEFAULT_PORT
import DEFAULT_UI_PORT
import GITHUB_OATH_APP_SECRET
import GITHUB_OAUTH_APP_CLIENT_ID
import kotlinx.serialization.Serializable

//TODO: Figure out kotlinx.serialisation to convert from snake case to camel case automatically

enum class GithubAuthorAssociation{
    CONTRIBUTOR,
    OWNER,
    NONE
}

@Serializable
enum class GithubPullRequestState{
    open,
    closed,
    all
}


@Serializable
data class RepoSummary(
    val name: String,
    val full_name: String,
    val private: Boolean
)

@Serializable
data class PullRequestSummary(
    val url: String,
    val id: Long,
    val node_id: String,
    val html_url: String,
    val diff_url: String,
    val patch_url: String,
    val issue_url: String,
    val number: Int,
    val state: GithubPullRequestState,
    val locked: Boolean,
    val title: String,
    val user: User,
    val body: String,
    val created_at: String,
    val updated_at: String,
    val closed_at: String?,
    val merged_at: String?,
    val merge_commit_sha: String? = null,
    val assignee: User?,
    val assignees: List<User>,
    val requested_reviewers: List<User>,
    val requested_teams: List<GithubTeam>,
    val labels: List<GithubLabel>,
    val milestone: GithubMilestone?,
    val draft: Boolean,
    val commits_url: String,
    val review_comments_url: String,
    val review_comment_url: String,
    val comments_url: String,
    val statuses_url: String,
    val head: GithubGitRef,
    val base: GithubGitRef,
    val _links: GithubLinkRelations,
    val author_association: GithubAuthorAssociation
)

@Serializable
data class PullRequestDetails(
    val url: String,
    val id: Long,
    val node_id: String,
    val html_url: String,
    val diff_url: String,
    val patch_url: String,
    val issue_url: String,
    val commits_url: String,
    val review_comments_url: String,
    val review_comment_url: String,
    val comments_url: String,
    val statuses_url: String,
    val number: Int,
    val state: GithubPullRequestState,
    val locked: Boolean,
    val title: String,
    val user: User,
    val body: String,
    val labels: List<GithubLabel>,
    val milestone: GithubMilestone?,
    val active_lock_reason: String? = null,
    val created_at: String,
    val updated_at: String,
    val closed_at: String?,
    val merged_at: String?,
    val merge_commit_sha: String?,
    val assignee: User?,
    val assignees: List<User>,
    val requested_reviewers: List<User>,
    val requested_teams: List<GithubTeam>,
    val head: GithubGitRef,
    val base: GithubGitRef,
    val _links: GithubLinkRelations,
    val author_association: GithubAuthorAssociation,
    val draft: Boolean,
    val merged: Boolean,
    val mergeable: Boolean,
    val rebaseable: Boolean,
    val mergeable_state: String, //TODO Convert to Enum
    val merged_by: User?,
    val comments: Long,
    val review_comments: Long,
    val maintainer_can_modify: Boolean,
    val commits: Long,
    val additions: Long,
    val deletions: Long,
    val changed_files: Long
)

@Serializable
data class PullRequestUpdate(
    val title: String,
    val body: String,
    val state: GithubPullRequestState,
    /**
     * The name of the branch you want your changes pulled into.
     * This should be an existing branch on the current repository.
     * You cannot update the base branch on a pull request to point to another repository.
     */
    val base: String,
    val maintainer_can_modify: Boolean
)

@Serializable
data class GithubCommit(
    val url: String,
    val sha: String,
    val node_id: String,
    val html_url: String,
    val comments_url: String,
    val commit: GithubCommitDetails,
    val author: User,
    val committer: User,
    val parents: List<TreeRef>
)

@Serializable
data class GithubCommitDetails(
    val url: String,
    val author: Map<String, String>,
    val committer: Map<String, String>,
    val message: String,
    val tree: TreeRef,
    val comment_count: Long,
    val verification: CommitVerificationDetails
)

@Serializable
data class PullRequestFileDetails(
    val sha: String,
    val filename: String,
    val status: String, // TODO: convert to enum
    val additions: Long,
    val deletions: Long,
    val changes: Long,
    val blob_url: String,
    val raw_url: String,
    val contents_url: String,
    val patch: String
)

@Serializable
data class TreeRef(
    val url: String,
    val sha: String
)

@Serializable
data class CommitVerificationDetails(
    val verified: Boolean,
    val reason: String,
    val signature: String? = null,
    val payload: String? = null
)

enum class GithubMergeMethod {
    merge,
    squash,
    rebase
}

@Serializable
data class MergePullRequest(
    val commit_title: String,
    val commit_message: String,
    val sha: String,
    val merge_method: GithubMergeMethod
)

@Serializable
data class GithubTeam(
    val id: Long,
    val node_id: String,
    val url: String,
    val html_url: String,
    val name: String,
    val slug: String,
    val description: String,
    val privacy: String,
    val permission: String,
    val members_url: String,
    val repositories_url: String,
    val parent: String?// TODO: Fix
)

@Serializable
data class GithubMilestone(
    val url: String,
    val html_url: String,
    val labels_url: String,
    val id: Long,
    val node_id: String,
    val number: Int,
    val state: String,
    val description: String,
    val creator: User,
    val open_issues: Long,
    val closed_issues: Long,
    val created_at: String,
    val updated_at: String,
    val closed_at: String?,
    val due_on: String
)

@Serializable
data class GithubLabel(
    val id: Long,
    val node_id: String,
    val url: String,
    val name: String,
    val description: String,
    val color: String,
    val default: Boolean
)

@Serializable
data class GithubLink(
    val href: String
) {
    override fun toString(): String {
        return href
    }
}

@Serializable
data class GithubLinkRelations(
    val self: GithubLink? = null,
    val html: GithubLink,
    val issue: GithubLink? = null,
    val comments: GithubLink? = null,
    val review_comments: GithubLink? = null,
    val review_comment: GithubLink? = null,
    val commits: GithubLink? = null,
    val statuses: GithubLink? = null
)

@Serializable
data class GithubGitRef(
    val label: String,
    val ref: String,
    val sha: String,
    val user: User,
    val repo: Repo
)

@Serializable
data class Repo(
    val id: Long,
    val node_id: String,
    val name: String,
    val full_name: String,
    val private: Boolean,
    val owner: User,
    val html_url: String,
    val description: String? = null,
    val fork: Boolean,
    val url: String,
    val forks_url: String,
    val keys_url: String,
    val collaborators_url: String,
    val teams_url: String,
    val hooks_url: String,
    val issue_events_url: String,
    val events_url: String,
    val assignees_url: String,
    val branches_url: String,
    val tags_url: String,
    val blobs_url: String,
    val git_tags_url: String,
    val git_refs_url: String,
    val trees_url: String,
    val statuses_url: String,
    val languages_url: String,
    val stargazers_url: String,
    val contributors_url: String,
    val subscribers_url: String,
    val subscription_url: String,
    val commits_url: String,
    val git_commits_url: String,
    val comments_url: String,
    val issue_comment_url: String,
    val contents_url: String,
    val compare_url: String,
    val merges_url: String,
    val archive_url: String,
    val downloads_url: String,
    val issues_url: String,
    val pulls_url: String,
    val milestones_url: String,
    val notifications_url: String,
    val labels_url: String,
    val releases_url: String,
    val deployments_url: String,
    val created_at: String,
    val updated_at: String,
    val pushed_at: String,
    val git_url: String,
    val ssh_url: String,
    val clone_url: String,
    val svn_url: String,
    val homepage: String?,
    val size: Int,
    val stargazers_count: Long,
    val watchers_count: Long,
    val language: String?,
    val has_issues: Boolean,
    val has_projects: Boolean,
    val has_downloads: Boolean,
    val has_wiki: Boolean,
    val has_pages: Boolean,
    val forks_count: Long,
    val mirror_url: String?,
    val archived: Boolean,
    val disabled: Boolean,
    val open_issues_count: Long,
    val license: GithubLicenseInfo? = null,
    val forks: Long? = 0,
    val open_issues: Long? = 0,
    val watchers: Long? = 0,
    val default_branch: String
)

@Serializable
data class GithubLicenseInfo(
    val key: String,
    val name: String,
    val spdx_id: String,
    val url: String,
    val node_id: String
)

@Serializable
data class User(
    val login: String,
    val id: Long,
    val node_id: String,
    val avatar_url: String,
    val gravatar_id: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val starred_url: String,
    val subscriptions_url: String,
    val organizations_url: String,
    val repos_url: String,
    val events_url: String,
    val received_events_url: String,
    val type: String, //TODO: Convert to enum after reading docs
    val site_admin: Boolean
)

@Serializable
data class OrgSummary(
    val login: String,
    val id: Long,
    val node_id: String,
    val url: String,
    val repos_url: String,
    val events_url: String,
    val hooks_url: String,
    val issues_url: String,
    val members_url: String,
    val public_members_url: String,
    val avatar_url: String,
    val description: String
)

@Serializable
data class IssueComment(
    val id: Long,
    val node_id: String,
    val url: String,
    val html_url: String,
    val body: String,
    val user: User,
    val created_at: String,
    val updated_at: String
)

@Serializable
enum class ReviewState{
    APPROVED,
    PENDING,
    CHANGES_REQUESTED,
    COMMENTED
}

@Serializable
data class Review(
    val id: Long,
    val node_id: String,
    val user: User,
    val body: String,
    val state: ReviewState,
    val html_url: String,
    val pull_request_url: String,
    val _links: GithubLinkRelations,
    val submitted_at: String,
    val commit_id: String
)

@Serializable
data class ReviewComment(
    val url: String,
    val pull_request_review_id: Long,
    val id: Long,
    val node_id: String,
    val diff_hunk: String,
    val path: String,
    val position: Long,
    val original_position: Long,
    val commit_id: String,
    val original_commit_id: String,
    val in_reply_to_id: Long? = null,
    val user: User,
    val body: String,
    val created_at: String,
    val updated_at: String,
    val html_url: String,
    val pull_request_url: String,
    val author_association: GithubAuthorAssociation,
    val _links: GithubLinkRelations
)

@Serializable
data class ReviewCommentPayload(
    val path: String,
    val position: Int,
    val body: String
)

@Serializable
enum class ReviewPayloadEventState {
    APPROVE,
    REQUEST_CHANGES,
    COMMENT,
    PENDING
}

@Serializable
data class ReviewPayload(
    /** Defaults to the most recent commit in the pull request if not specified */
    val commit_id: String? = null,
    val body: String,
    val event: ReviewPayloadEventState,
    val comments: List<ReviewCommentPayload>
)

@Serializable
data class ReviewUpdatePayload(
    val body: String,
    val event: ReviewPayloadEventState
)

@Serializable
data class ReviewDismissPayload(
    val message: String
)

@Serializable
enum class ReviewCommentSide{
    RIGHT,
    LEFT
}

@Serializable
data class PullRequestReviewComment(
    val url: String,
    val pull_request_review_id: Long,
    val id: Long,
    val node_id: String,
    val diff_hunk: String,
    val path: String,
    val position: Long,
    val original_position: Long,
    val commit_id: String,
    val original_commit_id: String,
    val in_reply_to_id: Long? = null,
    val user: User,
    val body: String,
    val created_at: String,
    val updated_at: String,
    val html_url: String,
    val pull_request_url: String,
    val author_association: GithubAuthorAssociation,
    val _links: GithubLinkRelations,
    val start_line: Long? = null,
    val original_start_line: Long? = null,
    val start_side: ReviewCommentSide? = null,
    val line: Long? = null,
    val original_line: Long? = null,
    val side: ReviewCommentSide? = null
)

@Serializable
data class AuthorizeRequest(
    val client_id: String = GITHUB_OAUTH_APP_CLIENT_ID,
    val redirect_uri: String = "http://localhost:$DEFAULT_UI_PORT",
    val login: String,
    val scope: String = "repo",
    val state: String
)

@Serializable
data class AccessTokenRequest(
    val client_id: String = GITHUB_OAUTH_APP_CLIENT_ID,
    val redirect_uri: String = "http://localhost:$DEFAULT_UI_PORT",
    val client_secret: String = GITHUB_OATH_APP_SECRET,
    val code: String,
    val state: String
)

@Serializable
data class AccessTokenParams(
    val code: String,
    val state: String
)

/**
 * Eg: {"access_token":"e72e16c7e42f292c6912e7710c838347ae178b4a", "scope":"repo,gist", "token_type":"bearer"}
 */
@Serializable
data class AccessTokenResponse(
    val access_token: String,
    val scope: String,
    val token_type: String
)

@Serializable
data class InitiateLoginPacket(
    val login: String
)
