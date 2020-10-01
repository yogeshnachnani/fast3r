package supercr.workflows.codereview.screens

import codereview.FileDiffListV2
import codereview.FileDiffV2
import codereview.ReviewInfo
import datastructures.KeyboardShortcutTrie
import git.provider.PullRequestReviewComment
import git.provider.PullRequestSummary
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.css.margin
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.styledDiv
import supercr.kb.DiffViewShortcuts
import supercr.workflows.codereview.components.ActionBarShortcut
import supercr.workflows.codereview.components.FileReviewStatus
import supercr.workflows.codereview.components.fileList
import supercr.workflows.codereview.components.fileView
import codereview.FileDiffCommentHandler
import codereview.createCommentHandlerForFile
import codereview.retrieveChangedFileDiffList

external interface ChangeSetReviewScreenProps : RProps {
    var fileDiffList: FileDiffListV2
    var reviewInfo: ReviewInfo
    var pullRequestSummary: PullRequestSummary
    var onReviewDone: (ReviewInfo , FileDiffListV2) -> Unit
    var existingGithubComments: List<PullRequestReviewComment>
}

external interface ChangeSetReviewScreenState : RState {
    var selectedFile: FileDiffV2?
    var fileDiffShortutAndStatusList: List<FileDiffStateAndMetaData>
    var reviewDone: Boolean
}
data class FileDiffStateAndMetaData(
    val fileDiff: FileDiffV2,
    val assignedShortcut: String,
    val currentStatus: FileReviewStatus,
    val handler: () -> Unit,
    val commentHandler: FileDiffCommentHandler
)
/** TODO: There are too many places where state is being set. Fix that! */
class ChangeSetReviewScreen(
    constructorProps: ChangeSetReviewScreenProps
) : RComponent<ChangeSetReviewScreenProps, ChangeSetReviewScreenState>(constructorProps) {
    override fun ChangeSetReviewScreenState.init(props: ChangeSetReviewScreenProps) {
        fileDiffShortutAndStatusList = generateFileDiffAndMetaData()
        selectedFile = props.fileDiffList.fileDiffs.first()
        reviewDone = false
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                margin((-8).px)
            }
            fileList {
                fileList = state.fileDiffShortutAndStatusList
                selectedFile = state.selectedFile
            }
            if (state.selectedFile != null) {
                fileView {
                    fileDiff = props.fileDiffList.fileDiffs.find { it == state.selectedFile }!!
                    fileDiffCommentHandler = state.fileDiffShortutAndStatusList.first { it.fileDiff == state.selectedFile!! }.commentHandler
                    defaultActionBarActions = defaultActions
                }
            }
        }
    }

    private fun generateFileDiffAndMetaData(): List<FileDiffStateAndMetaData> {
        return KeyboardShortcutTrie.generateTwoLetterCombos(numberOfComponents = props.fileDiffList.fileDiffs.size)
            .mapIndexed { index, prefix ->
                val fileDiff = props.fileDiffList.fileDiffs[index]
                val handler = createHandlerFor(fileDiff)
                FileDiffStateAndMetaData(
                    fileDiff = fileDiff,
                    assignedShortcut = prefix,
                    currentStatus = FileReviewStatus.TO_BE_REVIEWED,
                    handler = handler,
                    commentHandler = createCommentHandlerForFile(
                        oldFile = fileDiff.oldFile,
                        newFile = fileDiff.newFile,
                    )
                )
            }
    }

    private fun createHandlerFor(fileDiff: FileDiffV2): () -> Unit {
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
    private val handleNextFileCommand: () -> Unit = {
        changeCurrentFileStateTo(FileReviewStatus.REVIEWED)
    }

    private val handleSaveForLater: () -> Unit = {
        changeCurrentFileStateTo(FileReviewStatus.SAVED_FOR_LATER)
    }

    private val defaultActions = listOf(
        ActionBarShortcut( DiffViewShortcuts.NextFile.label, DiffViewShortcuts.NextFile.shortcutString, handleNextFileCommand),
        ActionBarShortcut( DiffViewShortcuts.ReviewLater.label, DiffViewShortcuts.ReviewLater.shortcutString, handleSaveForLater)
    )

    private fun changeCurrentFileStateTo(statusToChangeTo: FileReviewStatus) {
        /** TODO: Possible bug here if the shortcut is called before any file is selected */
        val currentFileDiff = state.selectedFile!!
        val currentFileIndex = state.fileDiffShortutAndStatusList.indexOfFirst { it.fileDiff == currentFileDiff }
        val newFileSet = state.fileDiffShortutAndStatusList.mapIndexed { index, fileData ->
            if (index == currentFileIndex) {
                fileData.copy(currentStatus = statusToChangeTo)
            } else {
                fileData
            }
        }
        val pendingReview = newFileSet.filter { it.currentStatus == FileReviewStatus.TO_BE_REVIEWED }
        if (pendingReview.isEmpty()) {
            val firstSavedForLaterFile = newFileSet.firstOrNull { it.currentStatus == FileReviewStatus.SAVED_FOR_LATER }?.fileDiff
            val isReviewDone = firstSavedForLaterFile == null
            setState {
                selectedFile = firstSavedForLaterFile
                fileDiffShortutAndStatusList = newFileSet
                reviewDone = isReviewDone
            }
            if (isReviewDone) {
                props.onReviewDone(
                    props.reviewInfo ,
                    newFileSet.map { Pair(it.fileDiff, it.commentHandler) }.retrieveChangedFileDiffList()
                )
            }
        } else {
            /** Bring on the next file */
            val nextFile = pendingReview.first().fileDiff
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