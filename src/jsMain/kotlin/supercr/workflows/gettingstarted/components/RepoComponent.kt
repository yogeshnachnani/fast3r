package supercr.workflows.gettingstarted.components

import ListItem
import OutlinedInput
import codereview.Project
import codereview.SuperCrClient
import git.provider.RepoSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.Display
import kotlinx.css.LinearDimension
import kotlinx.css.display
import kotlinx.css.margin
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RReadableRef
import react.RState
import react.ReactElement
import react.createRef
import react.dom.p
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledInput
import styled.styledP
import supercr.css.ComponentStyles

external interface RepoComponentProps: RProps {
    var repoSummary: RepoSummary
    var guessedProject: Project
    var onLocalPathChange: (Project, String) -> Unit
}

external interface RepoComponentState: RState {
    var isProcessed: Boolean
}

class RepoComponent: RComponent<RepoComponentProps, RepoComponentState>() {

    private val inputRef: RReadableRef<HTMLTextAreaElement> = createRef()

    override fun RepoComponentState.init() {
        isProcessed = false
    }

    override fun RBuilder.render() {
        ListItem {
            attrs {
                button = false
                alignItems = "center"
                divider = true
                disabled = state.isProcessed
                key = props.repoSummary.full_name
            }
            styledDiv {
                css {
                    display = Display.flex
                    width = 600.px
                }
                styledP {
                    css {
                        display = Display.inlineBlock
                        margin(18.px)
                    }
                    +props.repoSummary.full_name
                }
                OutlinedInput {
                    attrs {
                        autoFocus = false
                        multiline = false
                        rows = 1
                        rowsMax = 1
                        placeholder = props.guessedProject.localPath
                        inputRef = this.inputRef
                        fullWidth = true
                        className = ComponentStyles.getClassName { ComponentStyles::repoInitialiserRepoPathInput }
                    }
                }
            }
        }
    }

}

internal fun RBuilder.repoComponent(handler: RepoComponentProps.() -> Unit): ReactElement {
    return child(RepoComponent::class) {
        this.attrs(handler)
    }
}
