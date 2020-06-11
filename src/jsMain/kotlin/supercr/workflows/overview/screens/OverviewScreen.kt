package supercr.workflows.overview.screens

import Grid
import codereview.Project
import git.provider.GithubClient
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import supercr.workflows.overview.components.projectList
import supercr.workflows.overview.components.pullRequestList
import supercr.workflows.overview.components.userStats

external interface OverviewScreenProps: RProps {
    var projects : List<Project>
    var getGithubClient: () -> GithubClient
}

external interface OverviewScreenState: RState {
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
                pullRequestList {
                    githubClient = props.getGithubClient()
                    projects = props.projects
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
