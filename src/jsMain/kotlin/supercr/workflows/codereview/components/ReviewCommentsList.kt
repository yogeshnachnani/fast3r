package supercr.workflows.codereview.components

import git.provider.GithubClient
import git.provider.PullRequestReviewComment
import git.provider.PullRequestSummary
import git.provider.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.styledDiv
import supercr.css.ComponentStyles
import supercr.utils.toDateTimeRepresentation

external interface ReviewCommentsListProps : RProps {
    var pullRequestSummary: PullRequestSummary
    var githubClient: GithubClient
    var existingGithubComments: List<PullRequestReviewComment>
}

external interface ReviewCommentsListState : RState {
    var reviews: List<Review>
}

class ReviewCommentsList : RComponent<ReviewCommentsListProps, ReviewCommentsListState>() {

    override fun ReviewCommentsListState.init() {
        reviews = listOf()
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                + ComponentStyles.reviewCommentsContainer
            }
            /** This is the Description posted by the original author */
            /** TODO: Fix this hack. Have this as a first class citizen*/
            pullRequestDescription {
                pullRequestSummary = props.pullRequestSummary
            }
            state.reviews
                .mapNotNull { aReview ->
                    if (aReview.body.isNotBlank() && aReview.body.isNotEmpty()) {
                        reviewComment {
                            review = aReview
                            reviewComments = props.existingGithubComments.filter { it.pull_request_review_id == aReview.id }
                        }
                    } else {
                        null
                    }
                }
        }
    }

    override fun componentDidMount() {
        GlobalScope.async(context = Dispatchers.Unconfined) {
            props.githubClient.listReviewsFor(props.pullRequestSummary)
                .let {
                    setState {
                        reviews = it
                    }
                }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                console.error("Could not load reviews in ReviewCommentList component")
                console.error(throwable)
            }
        }
    }
}

fun RBuilder.reviewCommentsList(handler: ReviewCommentsListProps.() -> Unit): ReactElement {
    return child(ReviewCommentsList::class) {
        this.attrs(handler)
    }
}