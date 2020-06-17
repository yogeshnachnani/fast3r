package supercr.workflows.codereview.screens

import Grid
import codereview.FileDiff
import codereview.FileDiffList
import datastructures.KeyboardShortcutTrie
import git.provider.PullRequestSummary
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import supercr.kb.UniversalKeyboardShortcutHandler
import supercr.workflows.codereview.components.FileDiffStateAndMetaData
import supercr.workflows.codereview.components.FileReviewStatus
import supercr.workflows.codereview.components.fileList
import supercr.workflows.codereview.components.fileView

external interface ChangeSetReviewScreenProps : RProps {
    var fileDiffList: FileDiffList
    var pullRequestSummary: PullRequestSummary
}

external interface ChangeSetReviewScreenState : RState {
    var selectedFile: FileDiff?
    var fileDiffShortutAndStatusList: List<FileDiffStateAndMetaData>
}

class ChangeSetReviewScreen(
    constructorProps: ChangeSetReviewScreenProps
) : RComponent<ChangeSetReviewScreenProps, ChangeSetReviewScreenState>(constructorProps) {

    override fun ChangeSetReviewScreenState.init(props: ChangeSetReviewScreenProps) {
        fileDiffShortutAndStatusList = generateFileDiffAndMetaData()
        selectedFile = props.fileDiffList.fileDiffs.first()
    }

    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "flex-start"
                spacing = 2
            }
            Grid {
                attrs {
                    item = true
                    container = false
                }
                fileList {
                    fileList = state.fileDiffShortutAndStatusList
                    selectedFile = state.selectedFile
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

    override fun componentDidMount() {
        registerCommandShortcuts()
    }

    override fun componentWillUnmount() {
        unregisterCommandShortcuts()
    }
    
    private fun generateFileDiffAndMetaData(): List<FileDiffStateAndMetaData> {
        return KeyboardShortcutTrie.generatePossiblePrefixCombos(
            prefixString = null,
            numberOfComponents = props.fileDiffList.fileDiffs.size
        )
            .mapIndexed { index, prefix ->
                val fileDiff = props.fileDiffList.fileDiffs[index]
                val handler = createHandlerFor(fileDiff)
                FileDiffStateAndMetaData(
                    fileDiff = fileDiff,
                    assignedShortcut = prefix,
                    currentStatus = FileReviewStatus.TO_BE_REVIEWED,
                    handler = handler
                )
            }
    }
    private fun createHandlerFor(fileDiff: FileDiff): () -> Unit {
        return {
            val currentFileDiff = if (state.selectedFile != null && state.selectedFile == fileDiff) {
                /** Basically toggling */
                null
            } else {
                fileDiff
            }
            setState {
                selectedFile = currentFileDiff
            }
        }
    }
    private fun registerCommandShortcuts() {
        UniversalKeyboardShortcutHandler.registerShortcut("]]", handleNextFileCommand, {})
    }

    private fun unregisterCommandShortcuts() {
        UniversalKeyboardShortcutHandler.unRegisterShortcut("]]")
    }

    private val handleNextFileCommand: () -> Unit = {
        /** TODO: Possible bug here if the shortcut is called before any file is selected */
        val currentFileDiff = state.selectedFile!!
        val currentFileIndex = state.fileDiffShortutAndStatusList.indexOfFirst { it.fileDiff == currentFileDiff }
        val newFileSet = state.fileDiffShortutAndStatusList.mapIndexed { index, fileData ->
            if (index == currentFileIndex) {
                fileData.copy(currentStatus = FileReviewStatus.REVIEWED)
            } else {
                fileData
            }
        }
        if (currentFileIndex == state.fileDiffShortutAndStatusList.lastIndex) {
            /** We have reached the end of the review */
            setState {
                selectedFile = null
                fileDiffShortutAndStatusList = newFileSet
            }
        } else {
            /** Bring on the next file */
            val nextFile = state.fileDiffShortutAndStatusList[currentFileIndex + 1].fileDiff
            setState {
                selectedFile = nextFile
                fileDiffShortutAndStatusList = newFileSet
            }
        }
    }
}

fun RBuilder.changeSetReview(handler: ChangeSetReviewScreenProps.() -> Unit): ReactElement {
    return child(ChangeSetReviewScreen::class) {
        this.attrs(handler)
    }
}