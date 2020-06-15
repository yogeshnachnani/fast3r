package supercr.workflows.codereview.screens

import Grid
import codereview.FileDiff
import codereview.FileDiffList
import git.provider.PullRequestSummary
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import supercr.workflows.codereview.components.fileList
import supercr.workflows.codereview.components.fileView

external interface ChangeSetReviewScreenProps : RProps {
    var fileDiffList: FileDiffList
    var pullRequestSummary: PullRequestSummary
}

external interface ChangeSetReviewScreenState : RState {
    var selectedFile: FileDiff?
}

class ChangeSetReviewScreen : RComponent<ChangeSetReviewScreenProps, ChangeSetReviewScreenState>() {
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
                    md = 10
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

fun RBuilder.changeSetReview(handler: ChangeSetReviewScreenProps.() -> Unit): ReactElement {
    return child(ChangeSetReviewScreen::class) {
        this.attrs(handler)
    }
}