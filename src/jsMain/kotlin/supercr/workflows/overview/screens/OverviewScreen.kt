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
import kotlinx.coroutines.invoke
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
import supercr.workflows.overview.data.PullRequestInfo

external interface OverviewScreenProps: RProps {
    var projects : List<Project>
    var getGithubClient: () -> GithubClient
    var superCrClient: SuperCrClient
}

external interface OverviewScreenState: RState {
    var pullRequests: List<PullRequestInfo>
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
            pullRequestSummary = state.pullRequests[state.selectedPullRequestIndex].pullRequestSummary
            project = state.pullRequests[state.selectedPullRequestIndex].project
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
        GlobalScope.async(context = Dispatchers.Main) {
            /**
             * First, we retrieve all the PRs per project and assign a keyboard shortcut to each PR
             * This is done synchronously first since keyboard shortcuts need to be assigned all at once
             * If we process each project in parallel, we may end up in a race condition where the same
             * keyboard shortcut is assigned to multiple PRs resulting in a runtime exception
             */
            val projectAndPrSummary = props.projects.flatMap { project ->
                val retrievedPullRequests = props.getGithubClient().listPullRequests(project)
                retrievedPullRequests.mapIndexed { index, pullRequestSummary ->
                    Pair(project, pullRequestSummary )
                }
            }
            val kbShortcuts = KeyboardShortcutTrie.generateTwoLetterCombos(numberOfComponents = projectAndPrSummary.size)
                /**
                 * Now, process each PR in parallel - retrieve it's diff from the backend so we have the Tshirt size for each
                 */
            projectAndPrSummary
                .mapIndexed { index,  (project, pullRequestSummary) ->
                    val assignedKeyboardShortcut = kbShortcuts[index]
                    GlobalScope.async(context = Dispatchers.Main) {
                        /** TODO : This actually determines the entire diff in the backend. Once computed, this should ideally be passed around in the UI*/
                        val fileDiffListV2 = props.superCrClient.getDiff(project, pullRequestSummary)
                        console.log("Retrieved fileDiff List of size ${fileDiffListV2.fileDiffs.size} and t shirt size ${fileDiffListV2.diffTShirtSize} for pr: ${pullRequestSummary.title} with shortcut $assignedKeyboardShortcut")
                        val pullRequestInfo = PullRequestInfo(project, pullRequestSummary, fileDiffListV2, assignedKeyboardShortcut)
                        val existingPrInfo = state.pullRequests
                        /** TODO : Figure out if it's better to update the state in one shot or for each retrieval like this */
                        setState {
                            pullRequests = existingPrInfo.plus(pullRequestInfo).distinctBy { it.pullRequestSummary.title }
                        }
                    } .invokeOnCompletion { throwable ->
                        if (throwable != null) {
                            console.error("Something bad happened. Could not retrieve file diff for ${project.name} and pr with title ${pullRequestSummary.title} and shortcut $assignedKeyboardShortcut")
                            console.error(throwable)
                        }
                    }
                }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Something bad happened. Could not retrieve pull requests for known projects")
                console.error(throwable)
            }
        }
    }

}

fun RBuilder.overviewScreen(handler: OverviewScreenProps.() -> Unit): ReactElement {
    return child(OverviewScreen::class) {
        this.attrs(handler)
    }
}
