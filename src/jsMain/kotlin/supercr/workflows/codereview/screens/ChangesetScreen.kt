package supercr.workflows.codereview.screens

import Grid
import codereview.FileDiff
import codereview.FileDiffList
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import supercr.workflows.codereview.components.fileList
import supercr.workflows.codereview.components.fileView

external interface ChangesetScreenProps: RProps {
    var fileDiffList: FileDiffList
}

external interface ChangeSetScreenState: RState {
    var selectedFile: FileDiff?
}

/**
 * Main Screen to start off reviews with.
 * Should aim to show
 * (a) Summary of the PR
 * (b) Files changed etc
 * (c) Stats
 * (d) Comments
 */
class ChangesetScreen : RComponent<ChangesetScreenProps, ChangeSetScreenState>() {
    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "flex-start"
                spacing = 1
            }
            Grid {
                attrs {
                    md = 3
                    item = true
                    container = false
                }
                fileList {
                    fileList = props.fileDiffList
                    onFileSelect = handleFileSelect
                }
            }
            Grid {
                attrs {
                    md = 9
                    item = true
                    container = false
                }
                if (state.selectedFile != null) {
                    fileView {
                        fileDiff = props.fileDiffList.fileDiffs.find { it == state.selectedFile }!!
                    }
                }
            }
        }
    }

    private val handleFileSelect: (FileDiff?) -> Unit = { userSelectedFile ->
        setState {
            selectedFile = userSelectedFile
        }
    }
}

fun RBuilder.changeSetScreen(handler: ChangesetScreenProps.() -> Unit): ReactElement {
    return child(ChangesetScreen::class) {
        this.attrs(handler)
    }
}
