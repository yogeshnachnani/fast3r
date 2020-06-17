package supercr.workflows.codereview.components

import Grid
import Paper
import kotlinx.css.color
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.buildElement
import styled.css
import styled.getClassName
import styled.styledP
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
        Paper {
            attrs {
                className = ComponentStyles.getClassName { ComponentStyles::actionBar }
                elevation = 3
            }
            Grid {
                attrs {
                    container = true
                    item = false
                    justify = "flex-start"
                    alignItems = "baseline"
                }
                props.actions.map {
                    renderAction(it)
                }
            }
        }
    }

    private fun RBuilder.renderAction(actionBarShortcut: ActionBarShortcut) {
        Grid {
            attrs {
                container = true
                item = false
                justify = "space-around"
                alignItems = "baseline"
                spacing = 1
                className = ComponentStyles.getClassName { ComponentStyles::actionBarItem }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                }
                styledP {
                    css {
                        color = Colors.warmGreyBase
                    }
                    + actionBarShortcut.textToDisplay
                }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                }
                keyboardChip {
                    attrs {
                        onSelected = actionBarShortcut.handler
                        assignedShortcut = actionBarShortcut.assignedShortcut
                        uponUnmount = handleShortcutUnmount
                    }
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