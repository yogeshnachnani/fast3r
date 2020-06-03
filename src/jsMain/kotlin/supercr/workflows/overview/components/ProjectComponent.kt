package supercr.workflows.overview.components

import codereview.Project
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.span

external interface ProjectComponentProps: RProps {
    var project: Project
}

class ProjectComponent: RComponent<ProjectComponentProps, RState>() {
    override fun RBuilder.render() {
        span {
            + props.project.name
        }
    }

}

fun RBuilder.projectComponent(handler: ProjectComponentProps.() -> Unit): ReactElement {
    return child(ProjectComponent::class) {
        this.attrs(handler)
    }
}
