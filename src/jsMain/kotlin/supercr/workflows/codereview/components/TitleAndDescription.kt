package supercr.workflows.codereview.components

import Paper
import git.provider.PullRequestSummary
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.p

external interface TitleAndDescriptionProps : RProps {
    var pullRequestSummary: PullRequestSummary
}

external interface TitleAndDescriptionState : RState {

}

class TitleAndDescription : RComponent<TitleAndDescriptionProps, TitleAndDescriptionState>() {
    override fun RBuilder.render() {
        Paper {
            attrs {
                square = true
                variant = "outlined"
            }
            p {
                + "${ props.pullRequestSummary.title } #${props.pullRequestSummary.number}"
            }
            p {
                + props.pullRequestSummary.body
            }
        }
    }
}

fun RBuilder.titleAndDescription(handler: TitleAndDescriptionProps.() -> Unit): ReactElement {
    return child(TitleAndDescription::class) {
        this.attrs(handler)
    }
}