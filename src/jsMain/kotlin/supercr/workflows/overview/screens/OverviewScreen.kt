package supercr.workflows.overview.screens

import Grid
import codereview.FileDiffListV2
import codereview.Project
import codereview.ReviewInfo
import codereview.SuperCrClient
import datastructures.KeyboardShortcutTrie
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
import react.buildElement
import react.setState
import styled.css
import styled.styledP
import supercr.css.ComponentStyles
import supercr.kb.components.helpBox
import supercr.kb.components.iconAndLogoutButton
import supercr.kb.components.keyboardShortcutExplainer
import supercr.workflows.codereview.screens.changeSetScreen
import supercr.workflows.common.BaseScreen
import supercr.workflows.overview.components.pullRequestList

external interface OverviewScreenProps: RProps {
    var projects : List<Project>
    var getGithubClient: () -> GithubClient
    var superCrClient: SuperCrClient
}

external interface OverviewScreenState: RState {
    var pullRequests: List<Triple<Project, PullRequestSummary, String>>
    var selectedPullRequestIndex: Int
}

class OverviewScreen : BaseScreen<OverviewScreenProps, OverviewScreenState>() {

    override fun RBuilder.renderScreen() {
        if (state.selectedPullRequestIndex == -1) {
            renderPullRequestGrid()
        } else {
            renderChangeSetOverview()
        }
    }

    override fun OverviewScreenState.init() {
        pullRequests = emptyList()
        selectedPullRequestIndex = -1
    }

    private fun RBuilder.renderChangeSetOverview() {
        changeSetScreen {
            pullRequestSummary = state.pullRequests[state.selectedPullRequestIndex].second
            project = state.pullRequests[state.selectedPullRequestIndex].first
            superCrClient = props.superCrClient
            onReviewDone = handlePostReview
            githubClient = props.getGithubClient()
        }
    }

    override fun getHelpContents(): List<Pair<String, List<ReactElement>>> {
        return listOf(
            "Overview Screen" to listOf(
                buildElement {
                    keyboardShortcutExplainer {
                        keyboardShortcutString = ""
                        helpText = "You can view all Pull Requests pending on you across all your projects"
                    }
                }
            ),
            "General" to listOf(
                buildElement {
                    keyboardShortcutExplainer {
                        keyboardShortcutString = "dh"
                        helpText = "This is an example of a keyboard shortcut used in Fast3r. Pressing the combo will trigger the corresponding action."
                    }
                }
            )
        )
    }

    private fun RBuilder.renderPullRequestGrid() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "center"
                direction = "row"
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 12
                }
                iconAndLogoutButton {  }
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    xl = 8
                    md = 10
                    lg = 10
                }
                styledP {
                    css {
                        + ComponentStyles.overviewScreenNumPullRequests
                    }
                    + "${state.pullRequests.size} Pull Requests"
                }
                pullRequestList {
                    attrs {
                        pullRequests = state.pullRequests
                        onPullRequestSelect = handlePullRequestSelect
                    }
                }
            }
        }
    }

    private val handlePostReview: (ReviewInfo, FileDiffListV2) -> Unit = { reviewInfo,  updatedFileList ->
        GlobalScope.async(context = Dispatchers.Main) {
            props.superCrClient.postReview(reviewInfo, updatedFileList)
            setState {
                selectedPullRequestIndex = -1
            }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Something bad happened While posting review to backend")
                console.error(throwable)
            }
        }
    }

    private val handlePullRequestSelect: (Int) -> Unit = { selectedIndex ->
        setState {
            selectedPullRequestIndex = selectedIndex
        }
    }


    override fun componentDidMount() {
        // TODO: Instead of iterating over projects, see if you can retrieve the pull requests where a given user is the asignee
        props.projects.map { project ->
            GlobalScope.async(context = Dispatchers.Main) {
                props.getGithubClient().listPullRequests(project)
                    .let { retrievedPullRequests ->
                        val kbShortcuts = KeyboardShortcutTrie.generateTwoLetterCombos(numberOfComponents = retrievedPullRequests.size)
                        val newlyRetrievedPrs = retrievedPullRequests.mapIndexed { index, pullRequestSummary ->
                            Triple(project, pullRequestSummary, kbShortcuts[index])
                        }
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

}

fun RBuilder.overviewScreen(handler: OverviewScreenProps.() -> Unit): ReactElement {
    return child(OverviewScreen::class) {
        this.attrs(handler)
    }
}
