package supercr.workflows.overview.components

import Button
import Grid
import ListItem
import ListSubHeader
import MaterialUIList
import codereview.Project
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.getClassName
import supercr.css.ComponentStyles

external interface ProjectListProps: RProps {
    var projects: List<Project>
}

class ProjectList: RComponent<ProjectListProps, RState>() {
    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                spacing = 2
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 12
                }
                renderProjectList()
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 12
                }
                Button {
                    attrs {
                        variant = "contained"
                        color = "primary"
                    }
                    + "Add More"
                }
            }
        }
    }

    private fun RBuilder.renderProjectList(): ReactElement{
        return MaterialUIList {
            ListSubHeader {
                attrs {
                    className = ComponentStyles.getClassName { ComponentStyles::genericListHeader }
                }
                + "Projects"
            }
            props.projects.mapIndexed { index, currentProject ->
                ListItem {
                    attrs {
                        button = false
                        divider = true
                    }
                    projectComponent {
                        project = currentProject
                    }
                }
            }
        }
    }
}

fun RBuilder.projectList(handler: ProjectListProps.() -> Unit): ReactElement {
    return child(ProjectList::class) {
        this.attrs(handler)
    }
}


