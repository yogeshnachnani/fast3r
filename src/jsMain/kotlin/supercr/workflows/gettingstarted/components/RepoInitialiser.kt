package supercr.workflows.gettingstarted.components

import MaterialUIList
import codereview.Project
import codereview.SuperCrClient
import git.provider.GithubClient
import git.provider.RepoSummary
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.color
import kotlinx.css.margin
import kotlinx.css.maxWidth
import kotlinx.css.px
import kotlinx.css.width
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.styledDiv
import styled.styledP
import supercr.css.Colors
import supercr.kb.components.enterActivatedButton

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
        if (state.repoToDetectedProject.isNotEmpty()) {
            renderRepoList()
            styledDiv {
                css {
                    width = 190.px
                    maxWidth = 190.px
                    margin(18.px)
                }
                enterActivatedButton {
                    label = "Done"
                    onSelected = handleDone
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
                    color = Colors.baseText
                }
                + "We automatically mapped your github repos to existing code on your computer"
            }
        }
        MaterialUIList {
            state.repoToDetectedProject.map { (fetchedRepoSummary, detectedProject) ->
                repoComponent {
                    repoSummary = fetchedRepoSummary
                    onLocalPathChange = handleUpdatePathForProject
                    guessedProject = detectedProject
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
