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
                Triple("YN","15 Aug","As explained over the long call, its basically a trivial change to the CLI tool. Start with checking CliArgs -> Main -> Runner -> RuleSets -> Junk.\n" +
                    "Sure, it would be a lot simpler with something that did a basic code flow automatically but that's still in the works so lets just live with it till we have the feature"),
                Triple("YN","15 Aug","Yes, that makes total sense! Wish there was a code review product that actually makes this simpler! For now, lets get on a long call where I'll explain my design to you"),
                Triple("DT","13 Aug","I didn't understand what you mean by \"relativize paths\"? What are you trying to achieve & why? I wish there was something that showed me the code flow or even show me a design doc before I can start this review!"),
                Triple("YN","12 Aug","Relativize paths based on input path or working dir")
            ).map {(reviewCommentInitials, commentDate, comment) ->
                reviewComment {
                    commentBody = comment
                    initials = reviewCommentInitials
                    displayDate = commentDate
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