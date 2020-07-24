package supercr.workflows.codereview.components

import kotlinx.html.id
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.styledDiv
import supercr.css.ComponentStyles

external interface ExtraInfoWindowProps : RProps {
}

external interface ExtraInfoWindowState : RState {

}

class ExtraInfoWindow : RComponent<ExtraInfoWindowProps, ExtraInfoWindowState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                + ComponentStyles.extraInfoWindowContainer
            }
            attrs {
                id = "extra-info-div"
            }
            styledDiv {
                css {
                    + ComponentStyles.extraInfoWindowHeader
                }
            }
        }
    }
}

fun RBuilder.extraInfoWindow(handler: ExtraInfoWindowProps.() -> Unit): ReactElement {
    return child(ExtraInfoWindow::class) {
        this.attrs(handler)
    }
}