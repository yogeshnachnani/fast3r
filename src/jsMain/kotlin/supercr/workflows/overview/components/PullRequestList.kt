package supercr.workflows.overview.components

import Grid
import codereview.Project
import datastructures.KeyboardShortcutTrie
import git.provider.PullRequestSummary
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import react.ReactElement
import supercr.workflows.overview.data.PullRequestInfo

external interface PullRequestListProps: RProps {
    var pullRequests: List<PullRequestInfo>
    var onPullRequestSelect: (Int) -> Unit
}

external interface PullRequestListState: RState {
}

class PullRequestList constructor(
    constructorProps: PullRequestListProps
) : RComponent<PullRequestListProps, PullRequestListState>(constructorProps) {

    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                alignContent = "flex-start"
                spacing = 6
                justify = "flex-start"
            }
            props.pullRequests.mapIndexed { index, currentPullRequestInfo->
                Grid {
                    attrs {
                        item = true
                        container = false
                        md = 6
                        lg = 6
                        xl = 4
                    }
                    pullRequestSummaryCard {
                        pullRequestInfo = currentPullRequestInfo
                        onClickHandler = createPullRequestSelectHandler(index)
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

fun RBuilder.pullRequestList(handler: RElementBuilder<PullRequestListProps>.() -> Unit): ReactElement {
    return child(PullRequestList::class) {
        handler()
    }
}