package supercr.workflows.codereview.components

import Avatar
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
                        className = "${ComponentStyles.getClassName { ComponentStyles::avatarInitials }} ${if( props.initials == "YN" ) {ComponentStyles.getClassName { ComponentStyles::avatarOrangeBackground }} else {ComponentStyles.getClassName { ComponentStyles::avatarPurpleBackground }} }"
                    }
                    + props.initials
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
                        + if(props.initials == "YN") { "yogeshnachnani" } else { "amodm" }
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