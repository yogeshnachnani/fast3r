package supercr.kb.components

import kotlinx.css.height
import kotlinx.css.vh
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.styledDiv

external interface IconAndLogoutButtonProps : RProps {
}

external interface IconAndLogoutButtonState : RState {

}

class IconAndLogoutButton : RComponent<IconAndLogoutButtonProps, IconAndLogoutButtonState>() {
    override fun RBuilder.render() {
        styledDiv {
            /** This div will later house the logo etc */
            css {
                height = 16.vh
            }
        }
    }
}

fun RBuilder.iconAndLogoutButton(handler: IconAndLogoutButtonProps.() -> Unit): ReactElement {
    return child(IconAndLogoutButton::class) {
        this.attrs(handler)
    }
}