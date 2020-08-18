package supercr.workflows.codereview.components

import BookmarkBorder
import CheckBoxOutlined
import ExpandLess
import ExpandMore
import ListItem
import ListItemIcon
import MaterialUIList
import NotificationsNone
import codereview.FileDiffV2
import codereview.getUniqueIdentifier
import kotlinx.css.FontStyle
import kotlinx.css.FontWeight
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontStyle
import kotlinx.css.fontWeight
import kotlinx.css.lineHeight
import kotlinx.css.marginBottom
import kotlinx.css.marginTop
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import supercr.css.ComponentStyles
import supercr.css.FontFamilies
import supercr.css.FontSizes
import supercr.css.LineHeights
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
                listOf(FileReviewStatus.TO_BE_REVIEWED, FileReviewStatus.SAVED_FOR_LATER, FileReviewStatus.REVIEWED)
                    .map { reviewStatus ->
                        renderListSeparator(reviewStatus)
                        if (reviewStatus.shouldShow()) {
                            MaterialUIList {
                                attrs {
                                    className = ComponentStyles.getClassName { ComponentStyles::fileList }
                                }
                                props.fileList
                                    .filter { it.currentStatus == reviewStatus}
                                    .map { (currentFileDiff, assignedShortcut, _, handler) ->
                                        renderFileItem(currentFileDiff, assignedShortcut, handler)
                                    }
                            }
                        }
                    }
            }
        }
    }

    private fun RBuilder.renderListSeparator(fileReviewStatus: FileReviewStatus) {
        ListItem {
            attrs {
                className = ComponentStyles.getClassName { ComponentStyles::fileListHeaderItem }
                button = true
                onClick = fileReviewStatus.getClickHandler()
            }
            ListItemIcon {
                attrs {
                    className = ComponentStyles.getClassName { ComponentStyles::fileListHeaderIcon  }
                }
                getIcon(fileReviewStatus)
            }
            styledP {
                css {
                    + ComponentStyles.fileListHeaderText
                }
                + fileReviewStatus.displayText()
            }
            styledDiv {
                css {
                    + ComponentStyles.fileListExpandIcon
                }
                if (fileReviewStatus.shouldShow()) {
                    ExpandLess {}
                } else {
                    ExpandMore {}
                }
            }
        }
    }

    private fun RBuilder.renderFileItem(currentFileDiff: FileDiffV2, assignedShortcutString: String, assignedHandler: () -> Unit) {
        fileItem {
            fileDiff = currentFileDiff
            isSelected = currentFileDiff.isGivenFilePresentlySelected()
            handlerForFile = assignedHandler
            assignedShortcut = assignedShortcutString
        }
    }

    private fun FileDiffV2.isGivenFilePresentlySelected() =
        props.selectedFile?.getUniqueIdentifier() == this.getUniqueIdentifier()


    private fun FileReviewStatus.displayText(): String {
        return when(this) {
            FileReviewStatus.TO_BE_REVIEWED -> "Pending Review"
            FileReviewStatus.REVIEWED -> "Done"
            FileReviewStatus.SAVED_FOR_LATER -> "Marked for Later"
        }
    }

    private fun FileReviewStatus.shouldShow(): Boolean {
        return when(this) {
            FileReviewStatus.TO_BE_REVIEWED -> state.showToBeReviewedList
            FileReviewStatus.REVIEWED -> state.showReviewedList
            FileReviewStatus.SAVED_FOR_LATER -> state.showSavedForLaterList
        }
    }

    private fun FileReviewStatus.getClickHandler(): () -> Unit {
        return when(this) {
            FileReviewStatus.TO_BE_REVIEWED -> {
                {
                    setState {
                        showToBeReviewedList = state.showToBeReviewedList.not()
                    }
                }
            }
            FileReviewStatus.REVIEWED -> {
                {
                    setState {
                        showReviewedList = state.showReviewedList.not()
                    }
                }
            }
            FileReviewStatus.SAVED_FOR_LATER -> {
                {
                    setState {
                        showSavedForLaterList = state.showSavedForLaterList.not()
                    }
                }
            }
        }
    }

    private fun RBuilder.getIcon(fileReviewStatus: FileReviewStatus) {
        when(fileReviewStatus) {
            FileReviewStatus.TO_BE_REVIEWED -> NotificationsNone {}
            FileReviewStatus.REVIEWED -> CheckBoxOutlined {}
            FileReviewStatus.SAVED_FOR_LATER -> BookmarkBorder {}
        }
    }

}
fun RBuilder.fileList(handler: FileListViewProps.() -> Unit): ReactElement {
    return child(FileListView::class) {
        this.attrs(handler)
    }
}


