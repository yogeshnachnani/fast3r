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
import kotlinx.css.Display
import kotlinx.css.FontWeight
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.marginLeft
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.getClassName
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.FontSizes

external interface PullRequestListProps: RProps {
    var pullRequests: List<Pair<Project, PullRequestSummary>>
}

external interface PullRequestListState: RState {
}

class PullRequestList : RComponent<PullRequestListProps, PullRequestListState>() {
    override fun RBuilder.render() {
        MaterialUIList {
            ListSubHeader {
                attrs {
                    className = ComponentStyles.getClassName { ComponentStyles::genericListHeader }
                }
                + "Pull Requests"
            }
            props.pullRequests.mapIndexed { index, (project, currentPullRequest) ->
                ListItem {
                    attrs {
                        button = true
                        divider = true

                    }
                    styledSpan {
                        css {
                            marginLeft = 8.px
                            fontSize = FontSizes.medium
                        }
                        + " ${currentPullRequest.title}  | "
                    }
                    styledSpan {
                        css {
                            display = Display.inlineBlock
                            marginLeft = 4.px
                            fontWeight = FontWeight.w700
                            color = Colors.warmGrey5
                        }
                        +project.name
                    }
                }
            }
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