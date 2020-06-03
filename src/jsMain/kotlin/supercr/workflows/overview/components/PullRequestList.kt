package supercr.workflows.overview.components

import ListItem
import ListSubHeader
import MaterialUIList
import codereview.Project
import git.provider.GithubClient
import git.provider.PullRequestSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState

external interface PullRequestListProps: RProps {
    var githubClient: GithubClient
    var projects: List<Project>
}

external interface PullRequestListState: RState {
    var pullRequests: List<PullRequestSummary>
}

class PullRequestList : RComponent<PullRequestListProps, PullRequestListState>() {
    override fun RBuilder.render() {
        MaterialUIList {
            ListSubHeader {
                + "Pull Requests"
            }
            state.pullRequests.mapIndexed { index, currentPullRequest ->
                ListItem {
                    attrs {
                        button = true
                        divider = true
                    }
                    + currentPullRequest.title
                }
            }
        }
    }

    override fun componentDidMount() {
        // TODO: Instead of iterating over projects, see if you can retrieve the pull requests where a given user is the asignee
        props.projects.map { project ->
            GlobalScope.async(context = Dispatchers.Main) {
                props.githubClient.listPullRequests(project)
                    .let { retrievedPullRequests ->
                        /** TODO : Figure out if it's better to update the state in one shot or for each retrieval like this */
                        setState {
                            pullRequests += retrievedPullRequests
                        }
                    }
            }.invokeOnCompletion { throwable ->
                if (throwable != null) {
                    console.error("Something bad happened")
                    console.error(throwable)
                }
            }
        }
    }

    override fun PullRequestListState.init() {
        pullRequests = emptyList()
    }
}

fun RBuilder.pullRequestList(handler: PullRequestListProps.() -> Unit): ReactElement {
    return child(PullRequestList::class) {
        this.attrs(handler)
    }
}