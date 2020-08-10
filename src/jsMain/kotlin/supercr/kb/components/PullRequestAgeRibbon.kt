package supercr.kb.components

import git.provider.PullRequestSummary
import kotlinx.css.backgroundColor
import kotlinx.css.borderBottomRightRadius
import kotlinx.css.borderTopRightRadius
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.styledDiv
import styled.styledSpan
import supercr.css.ComponentStyles
import supercr.utils.getAgeFromNow
import supercr.utils.pickAgeRibbonColor

external interface PullRequestAgeRibbonProps : RProps {
    var pullRequestSummary: PullRequestSummary
    var roundedBothSides: Boolean
}

external interface PullRequestAgeRibbonState : RState {

}

class PullRequestAgeRibbon : RComponent<PullRequestAgeRibbonProps, PullRequestAgeRibbonState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                + ComponentStyles.pullRequestSummaryAgeRibbon
                if (props.roundedBothSides) {
                    borderTopRightRadius = 100.px
                    borderBottomRightRadius = 100.px
                } else {
                    borderTopRightRadius = 0.px
                    borderBottomRightRadius = 0.px
                }
                backgroundColor = props.pullRequestSummary.pickAgeRibbonColor()
            }
            styledSpan {
                css {
                    + ComponentStyles.pullRequestSummaryAgeText
                }
                +props.pullRequestSummary.created_at.getAgeFromNow()
            }
        }
    }
}

fun RBuilder.pullRequestAgeRibbon(handler: PullRequestAgeRibbonProps.() -> Unit): ReactElement {
    return child(PullRequestAgeRibbon::class) {
        this.attrs(handler)
    }
}