package supercr.workflows.overview.components

import Paper
import kotlinx.css.VerticalAlign
import kotlinx.css.height
import kotlinx.css.marginTop
import kotlinx.css.px
import kotlinx.css.verticalAlign
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.getClassName
import styled.styledDiv
import supercr.css.ComponentStyles

external interface UserStatsProps : RProps {
}

external interface UserStatsState : RState {

}

class UserStats : RComponent<UserStatsProps, UserStatsState>() {
    override fun RBuilder.render() {
        Paper {
            attrs {
                className = ComponentStyles.getClassName { ComponentStyles::infoPaper }
            }
            styledDiv {
                css {
                    height = 150.px
                    marginTop = 40.px
                }
                + "Space for user Stats"
            }
        }
    }
}

fun RBuilder.userStats(handler: UserStatsProps.() -> Unit): ReactElement {
    return child(UserStats::class) {
        this.attrs(handler)
    }
}