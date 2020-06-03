package supercr.workflows.gettingstarted.components

import Button
import MaterialUIList
import codereview.Project
import codereview.SuperCrClient
import git.provider.GithubClient
import git.provider.RepoSummary
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState

interface RepoInitProps: RProps {
    var githubClient: GithubClient
    var superCrClient: SuperCrClient
    var passProjectInfo: (List<Project>) -> Unit
}

interface RepoInitState: RState {
    var repoList: List<RepoSummary>
    var showNextButton: Boolean
    var selectedProjects: List<Project>
}

class RepoInitComponent: RComponent<RepoInitProps, RepoInitState>() {
    override fun RepoInitState.init() {
        repoList = emptyList()
        showNextButton = false
        selectedProjects = emptyList()
    }

    override fun RBuilder.render() {
        MaterialUIList {
            state.repoList.map {
                repoComponent {
                    repoSummary = it
                    onSetupComplete = handleProjectSetup
                    superCrClient = props.superCrClient
                }
            }
        }
        if (state.showNextButton) {
            Button {
                attrs {
                    variant = "contained"
                    color = "primary"
                    onClick= handleDone
                }
                + "Done"
            }
        }
    }

    override fun componentDidMount() {
        fetchRepos()
    }

    private val handleDone: () -> Unit = {
        props.passProjectInfo(state.selectedProjects)
    }

    /**
     * TODO: Add some validations to check if the repo being selected is correct or no
     *  ** Selected Path should be a git repo
     *  ** Selected Path must point to the same origin (getOriginURL().contains(repoName))
     */
    private val handleProjectSetup: (Project) -> Boolean = { givenProject ->
        setState {
            showNextButton = true
            selectedProjects += givenProject
        }
        true
    }

    private fun fetchRepos() {
        GlobalScope.async {
            props.githubClient.getReposSummary("theboringtech")
                .let {
                    setState {
                        repoList = it
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
