package supercr.workflows.gettingstarted.components

import Grid
import ListItem
import MaterialUIList
import codereview.Project
import codereview.SuperCrClient
import git.provider.GithubClient
import git.provider.RepoSummary
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.LinearDimension
import kotlinx.css.alignContent
import kotlinx.css.alignItems
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.flex
import kotlinx.css.flexBasis
import kotlinx.css.fontSize
import kotlinx.css.justifyContent
import kotlinx.css.lineHeight
import kotlinx.css.map
import kotlinx.css.margin
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.maxWidth
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.FontSizes
import supercr.css.LineHeights
import supercr.kb.components.enterActivatedButton
import supercr.kb.components.iconAndLogoutButton

interface RepoInitProps: RProps {
    var githubClient: GithubClient
    var superCrClient: SuperCrClient
    var passProjectInfo: (List<Project>) -> Unit
}

interface RepoInitState: RState {
    var repoToDetectedProject: Map<RepoSummary, Project>
    var userFinalisedProjects: List<Project>
}

class RepoInitComponent: RComponent<RepoInitProps, RepoInitState>() {
    override fun RepoInitState.init() {
        repoToDetectedProject = emptyMap()
        userFinalisedProjects = emptyList()
    }

    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "center"
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 12
                }
                iconAndLogoutButton {  }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 9
                    lg = 9
                    xl = 5
                }
                renderRepoList()
                renderPageListAndActionButton()
            }
        }
    }

    private fun RBuilder.renderPageListAndActionButton() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "flex-start"
                alignItems = "center"
                spacing = 2
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 4
                }
                // Empty
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 6
                }
                styledDiv {
                    css {
                        + ComponentStyles.repoMappingActionButtonContainer
                    }
                    styledDiv {
                        css {
                            + ComponentStyles.repoMappingActionButtonHelpText
                        }
                        + "Press Enter ↵"
                    }
                    enterActivatedButton {
                        label = "Looks Good"
                        onSelected = handleDone
                        buttonClazz = ComponentStyles.getClassName { ComponentStyles::repoMappingActionButton }
                    }
                }
            }
        }
    }

    override fun componentDidMount() {
        fetchRepos()
    }

    private fun RBuilder.renderRepoList() {
        if (state.repoToDetectedProject.isNotEmpty()) {
            styledP {
                css {
                    + ComponentStyles.repoMappingTitle
                }
                + "We automatically mapped your github repos to existing code on your computer"
            }
            styledP {
                css {
                    +ComponentStyles.repoMappingSubtitle
                }
                + "${state.userFinalisedProjects.size} repo${if (state.userFinalisedProjects.size > 1) "s" else ""} found"
            }
            MaterialUIList {
                attrs {
                    className = ComponentStyles.getClassName { ComponentStyles::compactList }
                }
                state.repoToDetectedProject.map { (fetchedRepoSummary, detectedProject) ->
                    ListItem {
                        attrs {
                            button = false
                            alignItems = "center"
                            divider = true
                            disabled = false
                            key = fetchedRepoSummary.full_name
                            className = ComponentStyles.getClassName { ComponentStyles::compactList }
                        }
                        repoComponent {
                            repoSummary = fetchedRepoSummary
                            onLocalPathChange = handleUpdatePathForProject
                            guessedProject = detectedProject
                        }
                    }
                }
            }
        }
    }

    private val handleDone: () -> Unit = {
        props.passProjectInfo(state.userFinalisedProjects)
    }

    /**
     * TODO: Add some validations to check if the repo being selected is correct or no
     *  ** Selected Path should be a git repo
     *  ** Selected Path must point to the same origin (getOriginURL().contains(repoName))
     */
    private val handleUpdatePathForProject: (Project, String) -> Unit = { givenProject, newLocalPath ->
        val newProjectList = state.userFinalisedProjects.map {
            if (it.providerPath == givenProject.providerPath) {
                it.copy(localPath =  newLocalPath)
            } else {
                it
            }
        }
        setState {
            userFinalisedProjects = newProjectList
        }
    }

    private fun fetchRepos() {
        GlobalScope.async {
            val user = props.githubClient.getAuthenticatedUser()
            val userOrgs = props.githubClient.getAuthenticatedUserOrgs()
            userOrgs.map { orgSummary ->
                props.githubClient.getReposSummary(orgSummary.login)
            }
                .flatten()
                .plus(props.githubClient.getAuthenticatedUserRepos())
                .let { fetchedRepoSummaries ->
                    val detectedProjects = props.superCrClient.fetchDetectedProjectsFor(fetchedRepoSummaries)
                    setState {
                        repoToDetectedProject = detectedProjects
                        userFinalisedProjects = detectedProjects.values.toList()
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

fun RBuilder.repoInit(handler: RepoInitProps.() -> Unit): ReactElement {
    return child(RepoInitComponent::class) {
        this.attrs(handler)
    }
}
