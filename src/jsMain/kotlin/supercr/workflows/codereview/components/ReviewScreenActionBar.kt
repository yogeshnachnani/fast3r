package supercr.workflows.codereview.components

import Grid
import Paper
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.alignContent
import kotlinx.css.alignItems
import kotlinx.css.basis
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.flexBasis
import kotlinx.css.justifyContent
import kotlinx.css.marginBottom
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.maxWidth
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.html.id
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.buildElement
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.kb.components.keyboardChip

data class ActionBarShortcut(
    val textToDisplay: String,
    val assignedShortcut: String,
    val handler: () -> Unit
)

external interface ReviewScreenActionBarProps : RProps {
    var actions: List<ActionBarShortcut>
}

external interface ReviewScreenActionBarState : RState {

}

class ReviewScreenActionBar : RComponent<ReviewScreenActionBarProps, ReviewScreenActionBarState>() {
    override fun RBuilder.render() {
        styledDiv {
            attrs {
                id = "action-bar"
            }
            css {
                + ComponentStyles.actionBar
            }
            props.actions.map {
                renderAction(it)
            }
        }
    }

    private fun RBuilder.renderAction(actionBarShortcut: ActionBarShortcut) {
        styledDiv {
            css {
                + ComponentStyles.actionBarShortcutContainer
            }
            styledDiv {
                css {
                    display = Display.inlineFlex
                }
                + actionBarShortcut.textToDisplay
            }
            keyboardChip {
                attrs {
                    onSelected = actionBarShortcut.handler
                    assignedShortcut = actionBarShortcut.assignedShortcut
                    uponUnmount = handleShortcutUnmount
                    className = ComponentStyles.getClassName { ComponentStyles::actionBarKeyboardLetterBox }
                }
            }
        }
    }

    private val handleShortcutUnmount: (String) -> Unit = { _ ->
        //no op
    }
}

fun RBuilder.reviewScreenActionBar(handler: ReviewScreenActionBarProps.() -> Unit): ReactElement {
    return child(ReviewScreenActionBar::class) {
        this.attrs(handler)
    }
}