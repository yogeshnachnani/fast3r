package supercr.workflows.overview.components

import Grid
import codereview.Project
import datastructures.KeyboardShortcutTrie
import git.provider.PullRequestSummary
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement

external interface PullRequestListProps: RProps {
    var pullRequests: List<Pair<Project, PullRequestSummary>>
    var onPullRequestSelect: (Int) -> Unit
}

external interface PullRequestListState: RState {
}

class PullRequestList : RComponent<PullRequestListProps, PullRequestListState>() {
    override fun RBuilder.render() {
        val keyboardShortcuts = KeyboardShortcutTrie.generatePossiblePrefixCombos(
            prefixString = "d",
            numberOfComponents = props.pullRequests.size
        )
        Grid {
            attrs {
                container = true
                item = false
                alignContent = "flex-start"
                spacing = 6
                justify = "flex-start"
            }
            props.pullRequests.mapIndexed { index, (currentPRProject, currentPullRequest) ->
                Grid {
                    attrs {
                        item = true
                        container = false
                        md = 4
                    }
                    pullRequestSummaryCard {
                        project = currentPRProject
                        pullRequestSummary = currentPullRequest
                        onClickHandler = createPullRequestSelectHandler(index)
                        assignedKeyboardShortcut = keyboardShortcuts[index]
                    }
                }
            }
        }
    }

    private fun createPullRequestSelectHandler(pullRequestIndex: Int): () -> Unit {
        return {
            props.onPullRequestSelect(pullRequestIndex)
        }
    }

    override fun componentDidMount() {
    }
}

fun RBuilder.pullRequestList(handler: PullRequestListProps.() -> Unit): ReactElement {
    return child(PullRequestList::class) {
        this.attrs(handler)
    }
}