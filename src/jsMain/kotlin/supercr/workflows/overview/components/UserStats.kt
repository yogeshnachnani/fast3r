package supercr.workflows.overview.components

import Paper
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement

external interface UserStatsProps : RProps {
}

external interface UserStatsState : RState {

}

class UserStats : RComponent<UserStatsProps, UserStatsState>() {
    override fun RBuilder.render() {
        Paper {
            + "Stats Not implemented yet"
        }
    }
}

fun RBuilder.userStats(handler: UserStatsProps.() -> Unit): ReactElement {
    return child(UserStats::class) {
        this.attrs(handler)
    }
}