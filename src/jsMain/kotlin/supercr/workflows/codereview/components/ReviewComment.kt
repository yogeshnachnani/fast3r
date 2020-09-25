package supercr.workflows.codereview.components

import Avatar
import git.provider.User
import kotlinx.css.Display
import kotlinx.css.FontWeight
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.lineHeight
import kotlinx.css.marginLeft
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.FontSizes

external interface ReviewCommentProps : RProps {
    var commentBody: String
    var initials: String
    var displayDate: String
    var user: User
}

external interface ReviewCommentState : RState {

}

class ReviewComment : RComponent<ReviewCommentProps, ReviewCommentState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                + ComponentStyles.reviewCommentContainer
            }
            styledDiv {
                css {
                    + ComponentStyles.reviewCommentAvatarContainer
                }
                Avatar {
                    attrs {
                        src = props.user.avatar_url
                    }
                }
            }
            styledDiv {
                css {
                    + ComponentStyles.reviewCommentBox
                }
                /** User name and comment date */
                styledP {
                    styledSpan {
                        css {
                            fontWeight = FontWeight.w600
                        }
                        + props.user.login
                    }
                    styledSpan {
                        css {
                            color = Colors.textDarkGrey
                            fontWeight = FontWeight.w600
                            display = Display.inlineBlock
                            marginLeft = 12.px
                            fontSize = FontSizes.tiny
                        }
                        + props.displayDate
                    }
                }
                /** User Comment */
                styledP {
                    + props.commentBody
                }
            }
        }
    }
}

fun RBuilder.reviewComment(handler: ReviewCommentProps.() -> Unit): ReactElement {
    return child(ReviewComment::class) {
        this.attrs(handler)
    }
}