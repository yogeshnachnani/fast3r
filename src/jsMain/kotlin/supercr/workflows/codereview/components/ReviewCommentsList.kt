package supercr.workflows.codereview.components

import git.provider.GithubClient
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
            reviewComment {
                commentBody = props.pullRequestSummary.body
                /** TODO: Fix this */
                displayDate = props.pullRequestSummary.created_at.toDateTimeRepresentation()
                user = props.pullRequestSummary.user
            }
            state.reviews
                .mapNotNull { review ->
                    if (review.body.isNotBlank() && review.body.isNotEmpty()) {
                        reviewComment {
                            commentBody = review.body
                            displayDate = review.submitted_at.toDateTimeRepresentation()
                            user = review.user
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