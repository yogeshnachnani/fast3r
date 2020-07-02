package supercr.workflows.codereview.screens

import codereview.FileDiffListV2
import git.provider.PullRequestSummary
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState

external interface ChangeSetScreenProps : RProps {
    var pullRequestSummary: PullRequestSummary
    var fileDiffList: FileDiffListV2
    var onReviewDone: (FileDiffListV2) -> Unit
}

external interface ChangeSetScreenState : RState {
    var inReview: Boolean
}

/**
 * Entry point for a changeset. Shows the [ChangesetOverviewScreen] and facilitates transition to [ChangeSetReviewScreen]
 */
class ChangeSetScreen : RComponent<ChangeSetScreenProps, ChangeSetScreenState>() {

    override fun ChangeSetScreenState.init() {
        inReview = false
    }

    override fun RBuilder.render() {
        if(state.inReview) {
            renderReviewScreen()
        } else {
            renderOverviewScreen()
        }
    }

    private fun RBuilder.renderOverviewScreen() {
        changeSetOverviewScreen {
            pullRequestSummary = props.pullRequestSummary
            fileDiffList = props.fileDiffList
            handleStartReview = startReview
        }
    }

    private fun RBuilder.renderReviewScreen() {
        changeSetReview {
            pullRequestSummary = props.pullRequestSummary
            fileDiffList = props.fileDiffList
            onReviewDone = props.onReviewDone
        }
    }


    private val startReview: () -> Unit = {
        setState {
            inReview = true
        }
    }
}

fun RBuilder.changeSetScreen(handler: ChangeSetScreenProps.() -> Unit): ReactElement {
    return child(ChangeSetScreen::class) {
        this.attrs(handler)
    }
}