package supercr.kb.components

import codereview.Project
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.styledDiv
import supercr.css.ComponentStyles

external interface ProjectNameChipProps : RProps {
    var project: Project
}

external interface ProjectNameChipState : RState {

}

class ProjectNameChip : RComponent<ProjectNameChipProps, ProjectNameChipState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                + ComponentStyles.pullRequestSummaryProjectName
            }
            + props.project.name
        }
    }
}

fun RBuilder.projectNameChip(handler: ProjectNameChipProps.() -> Unit): ReactElement {
    return child(ProjectNameChip::class) {
        this.attrs(handler)
    }
}