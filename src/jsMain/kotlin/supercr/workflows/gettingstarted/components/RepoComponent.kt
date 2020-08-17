package supercr.workflows.gettingstarted.components

import Grid
import ListItem
import OutlinedInput
import codereview.Project
import codereview.SuperCrClient
import git.provider.RepoSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.LinearDimension
import kotlinx.css.TextAlign
import kotlinx.css.alignContent
import kotlinx.css.alignItems
import kotlinx.css.basis
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.flexBasis
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.justifyContent
import kotlinx.css.lineHeight
import kotlinx.css.margin
import kotlinx.css.marginBottom
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.textAlign
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
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.FontFamilies
import supercr.css.FontSizes
import supercr.css.LineHeights

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
        Grid {
            attrs {
                container = true
                item = false
                justify = "flex-start"
                className = ComponentStyles.getClassName { ComponentStyles::repoMappingRepoComponentContainer }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 5
                    className = ComponentStyles.getClassName { ComponentStyles::repoMappingRepoName }
                }
                + props.repoSummary.full_name
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 7
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
