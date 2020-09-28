package supercr.workflows.codereview.components

import Avatar
import git.provider.PullRequestSummary
import kotlinx.css.Display
import kotlinx.css.FontWeight
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.marginLeft
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.FontSizes
import supercr.utils.toDateTimeRepresentation

external interface PullRequestDescriptionProps : RProps {
    var pullRequestSummary: PullRequestSummary
}

external interface PullRequestDescriptionState : RState {

}

class PullRequestDescription : RComponent<PullRequestDescriptionProps, PullRequestDescriptionState>() {
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
                        src = props.pullRequestSummary.user.avatar_url
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
                        + props.pullRequestSummary.user.login
                    }
                    styledSpan {
                        css {
                            color = Colors.textDarkGrey
                            fontWeight = FontWeight.w600
                            display = Display.inlineBlock
                            marginLeft = 12.px
                            fontSize = FontSizes.tiny
                        }
                        + props.pullRequestSummary.created_at.toDateTimeRepresentation()
                    }
                }
                /** User Comment */
                styledP {
                    + props.pullRequestSummary.body
                }
            }
        }
    }
}

fun RBuilder.pullRequestDescription(handler: PullRequestDescriptionProps.() -> Unit): ReactElement {
    return child(PullRequestDescription::class) {
        this.attrs(handler)
    }
}