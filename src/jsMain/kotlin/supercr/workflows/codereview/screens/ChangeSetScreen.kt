package supercr.workflows.codereview.screens

import codereview.FileDiffListV2
import codereview.Project
import codereview.ReviewInfo
import codereview.SuperCrClient
import git.provider.PullRequestSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.styledP

external interface ChangeSetScreenProps : RProps {
    var pullRequestSummary: PullRequestSummary
    var project: Project
    var superCrClient: SuperCrClient
    var onReviewDone: (ReviewInfo ,FileDiffListV2) -> Unit
}

external interface ChangeSetScreenState : RState {
    var inReview: Boolean
    var fileDiffList: FileDiffListV2?
    var reviewInfo: ReviewInfo?
}

/**
 * Entry point for a changeset. Shows the [ChangesetOverviewScreen] and facilitates transition to [ChangeSetReviewScreen]
 */
class ChangeSetScreen : RComponent<ChangeSetScreenProps, ChangeSetScreenState>() {

    override fun ChangeSetScreenState.init() {
        inReview = false
        fileDiffList = null
    }

    override fun RBuilder.render() {
        if(state.inReview) {
            renderReviewScreen()
        } else {
            renderOverviewScreen()
        }
    }

    override fun componentDidMount() {
        GlobalScope.async(context = Dispatchers.Main) {
            val (createdReview, errString) = props.superCrClient.startReview(props.project, props.pullRequestSummary)
            val diff = props.superCrClient.getReviewDiff(createdReview!!, props.pullRequestSummary)
            setState {
                fileDiffList = diff
                reviewInfo = createdReview
            }
        }.invokeOnCompletion { throwable ->
        if (throwable != null) {
            console.error("Something bad happened")
            console.error(throwable)
        }
    }
    }

    private fun RBuilder.renderOverviewScreen() {
        if (state.fileDiffList != null) {
            changeSetOverviewScreen {
                pullRequestSummary = props.pullRequestSummary
                fileDiffList = state.fileDiffList!!
                handleStartReview = startReview
            }
        } else {
            styledP {
                + "loading.."
            }
        }
    }

    private fun RBuilder.renderReviewScreen() {
        changeSetReview {
            pullRequestSummary = props.pullRequestSummary
            fileDiffList = state.fileDiffList!!
            onReviewDone = props.onReviewDone
            reviewInfo = state.reviewInfo!!
        }
    }


    private val startReview: (FileDiffListV2) -> Unit = { orderedFileList ->
        setState {
            fileDiffList = orderedFileList
            inReview = true
        }
    }

}

fun RBuilder.changeSetScreen(handler: ChangeSetScreenProps.() -> Unit): ReactElement {
    return child(ChangeSetScreen::class) {
        this.attrs(handler)
    }
}