package supercr.workflows.codereview.components

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.styledDiv
import supercr.css.ComponentStyles

external interface ReviewCommentsListProps : RProps {
}

external interface ReviewCommentsListState : RState {

}

class ReviewCommentsList : RComponent<ReviewCommentsListProps, ReviewCommentsListState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                + ComponentStyles.reviewCommentsContainer
            }
            listOf(
                "This is a first test comment; which will be followed by",
                "This is a first test comment; which will be followed by",
                "This is a first test comment; which will be followed by",
                "Another test comment",
                "Another test comment",
                "Another test comment",
                "Another test comment",
                "Another test comment",
                "Another test comment",
                "Another test comment",
                "Another test comment"
            ).map {
                reviewComment {
                    commentBody = it
                }
            }
        }
    }
}

fun RBuilder.reviewCommentsList(handler: ReviewCommentsListProps.() -> Unit): ReactElement {
    return child(ReviewCommentsList::class) {
        this.attrs(handler)
    }
}