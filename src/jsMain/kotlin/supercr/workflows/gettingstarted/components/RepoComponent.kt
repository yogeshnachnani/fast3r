package supercr.workflows.gettingstarted.components

import ListItem
import codereview.Project
import codereview.SuperCrClient
import git.provider.RepoSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.createRef
import react.dom.p
import react.setState
import styled.css
import styled.styledInput

external interface RepoComponentProps: RProps {
    var superCrClient: SuperCrClient
    var repoSummary: RepoSummary
    var onSetupComplete: (Project) -> Boolean
}

external interface RepoComponentState: RState {
    var isProcessed: Boolean
}

class RepoComponent: RComponent<RepoComponentProps, RepoComponentState>() {
    private val inputFileRef = createRef<HTMLInputElement>()

    private val handleSelection: () -> Unit = {
//        inputFileRef.current!!.click() TODO See below. Below if condition is just a hack to select the 'right folder'
        val project = if (props.repoSummary.full_name == "theboringtech/btcmain") {
            Project(localPath = "/home/yogesh/work/btc", providerPath = props.repoSummary.full_name, name = props.repoSummary.name)
        } else {
            Project(localPath = "/home/yogesh/work/theboringtech.github.io", providerPath = props.repoSummary.full_name, name = props.repoSummary.name)
        }
        GlobalScope.async(context = Dispatchers.Main) {
            props.superCrClient.addProject(project)
                .let { successfullyProcessed ->
                    setState {
                        isProcessed = successfullyProcessed
                    }
                    if (successfullyProcessed) {
                        props.onSetupComplete(project)
                    }
                }
        }.invokeOnCompletion { cause: Throwable? ->
            if (cause != null) {
                console.error("Something bad happened")
                console.error(cause)
            }
        }
    }

    override fun RepoComponentState.init() {
        isProcessed = false
    }

    override fun RBuilder.render() {
        ListItem {
            attrs {
                button = true
                alignItems = "center"
                divider = true
                onClick = handleSelection
                disabled = state.isProcessed
            }
            p {
                +props.repoSummary.full_name
            }
            /** TODO: Make this work once we switch to electron or Kvision. Right now it just sits there - never used */
            styledInput {
                css {
                    display = Display.none
                }
                attrs {
                    ref = inputFileRef
                }
                attrs.onChangeFunction = { event ->
                    console.log("Got the value as ${inputFileRef.current?.files}")
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
