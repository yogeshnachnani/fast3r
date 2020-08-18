package supercr.workflows.codereview.components

import MaterialUIList
import codereview.FileDiffV2
import codereview.getUniqueIdentifier
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.getClassName
import styled.styledDiv
import supercr.css.ComponentStyles
import supercr.workflows.codereview.screens.FileDiffStateAndMetaData

enum class FileDiffListMode {
    compact,
    expanded
}

/**
 * Displays the list of files given by [fileList] prop.
 * Also enables keyboard access for each individual file
 * This Component manages the order in which the list of files are displayed.
 */
external interface FileListViewProps : RProps{
    var fileList: List<FileDiffStateAndMetaData>
    var mode: FileDiffListMode
    var selectedFile: FileDiffV2?
}

enum class FileReviewStatus {
    TO_BE_REVIEWED,
    REVIEWED,
    SAVED_FOR_LATER
}

external interface FileListViewState: RState {
    var showToBeReviewedList: Boolean
    var showSavedForLaterList: Boolean
    var showReviewedList: Boolean
}

class FileListView: RComponent<FileListViewProps, FileListViewState>() {

    override fun FileListViewState.init() {
        showReviewedList = false
        showToBeReviewedList = true
        showSavedForLaterList = false
    }

    override fun RBuilder.render() {
        if (props.fileList.isNotEmpty()) {
            styledDiv {
                css {
                    + ComponentStyles.fileListPane
                }
                styledDiv {
                    css {
                        + ComponentStyles.fileListHeaderItem
                    }
                    + "Files"
                }
                MaterialUIList {
                    attrs {
                        className = ComponentStyles.getClassName { ComponentStyles::fileList }
                    }
                    props.fileList
                        .map { (currentFileDiff, assignedShortcut, fileReviewStatus, handler) ->
                            renderFileItem(currentFileDiff, assignedShortcut, handler, fileReviewStatus)
                        }
                }
            }
        }
    }

    private fun RBuilder.renderFileItem(
        currentFileDiff: FileDiffV2,
        assignedShortcutString: String,
        assignedHandler: () -> Unit,
        fileReviewStatus: FileReviewStatus
    ) {
        fileItem {
            fileDiff = currentFileDiff
            isSelected = currentFileDiff.isGivenFilePresentlySelected()
            handlerForFile = assignedHandler
            assignedShortcut = assignedShortcutString
            reviewStatus = fileReviewStatus
        }
    }

    private fun FileDiffV2.isGivenFilePresentlySelected() =
        props.selectedFile?.getUniqueIdentifier() == this.getUniqueIdentifier()



}
fun RBuilder.fileList(handler: FileListViewProps.() -> Unit): ReactElement {
    return child(FileListView::class) {
        this.attrs(handler)
    }
}


