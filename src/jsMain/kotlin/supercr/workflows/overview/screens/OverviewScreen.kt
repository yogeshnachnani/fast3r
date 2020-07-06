package supercr.workflows.overview.screens

import Grid
import codereview.Project
import git.provider.GithubClient
import git.provider.PullRequestSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.height
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.styledDiv
import supercr.workflows.overview.components.projectList
import supercr.workflows.overview.components.pullRequestList
import supercr.workflows.overview.components.userStats

external interface OverviewScreenProps: RProps {
    var projects : List<Project>
    var getGithubClient: () -> GithubClient
}

external interface OverviewScreenState: RState {
    var pullRequests: List<Pair<Project, PullRequestSummary>>
}

class OverviewScreen : RComponent<OverviewScreenProps, OverviewScreenState>() {
    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "space-evenly"
                alignItems = "center"
                direction = "row"
                spacing = 1
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 8
                }
                styledDiv {
                    css {
                        height = 100.px
                    }
                }
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 8
                }
                pullRequestList {
                    pullRequests = state.pullRequests
                }
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 4
                }
                renderRightSection()
            }
        }
    }

    override fun OverviewScreenState.init() {
        pullRequests = emptyList()
    }

    override fun componentDidMount() {
        // TODO: Instead of iterating over projects, see if you can retrieve the pull requests where a given user is the asignee
        props.projects.map { project ->
            GlobalScope.async(context = Dispatchers.Main) {
                props.getGithubClient().listPullRequests(project)
                    .let { retrievedPullRequests ->
                        val newlyRetrievedPrs = retrievedPullRequests.map { Pair(project, it) }
                        val existingPrInfo = state.pullRequests
                        /** TODO : Figure out if it's better to update the state in one shot or for each retrieval like this */
                        setState {
                            pullRequests = existingPrInfo.plus(newlyRetrievedPrs).distinctBy { it.second.title }
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

    private fun RBuilder.renderRightSection(): ReactElement {
        return Grid {
            attrs {
                container = true
                item = false
                alignItems = "stretch"
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 12
                }
                userStats {

                }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 12
                }
                projectList {
                    projects = props.projects
                }
            }
        }
    }
}

fun RBuilder.overviewScreen(handler: OverviewScreenProps.() -> Unit): ReactElement {
    return child(OverviewScreen::class) {
        this.attrs(handler)
    }
}
