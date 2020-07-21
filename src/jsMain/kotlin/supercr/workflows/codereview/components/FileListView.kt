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
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.FontStyle
import kotlinx.css.FontWeight
import kotlinx.css.alignContent
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontStyle
import kotlinx.css.fontWeight
import kotlinx.css.lineHeight
import kotlinx.css.margin
import kotlinx.css.marginBottom
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.minHeight
import kotlinx.css.minWidth
import kotlinx.css.paddingBottom
import kotlinx.css.paddingTop
import kotlinx.css.pct
import kotlinx.css.px
import org.w3c.files.File
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.buildElement
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.FontFamilies
import supercr.css.FontSizes
import supercr.css.LineHeights
import supercr.kb.components.keyboardChip
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
                    minHeight = 20.px
                }
            }
            MaterialUIList {
                listOf(FileReviewStatus.TO_BE_REVIEWED, FileReviewStatus.SAVED_FOR_LATER, FileReviewStatus.REVIEWED)
                    .map { reviewStatus ->
                        renderListSeparator(reviewStatus)
                        if (reviewStatus.shouldShow()) {
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

    private fun RBuilder.renderListSeparator(fileReviewStatus: FileReviewStatus) {
        ListItem {
            attrs {
                divider = true
                className = ComponentStyles.getClassName { ComponentStyles::fileListItem }
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
                    fontFamily = FontFamilies.nonCode
                    fontStyle = FontStyle.normal
                    fontWeight = FontWeight.w600
                    fontSize = FontSizes.extraLarge
                    lineHeight = LineHeights.extraLarge
                    marginTop = 8.px
                    marginBottom = 8.px
                    color = Colors.textMediumGrey
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

    private fun RBuilder.renderFileItem(currentFileDiff: FileDiffV2, assignedShortcut: String, handlerForFile: () -> Unit) {
        ListItem {
            attrs {
                divider = false
                onClick = handlerForFile
                className = if (currentFileDiff.isGivenFilePresentlySelected()) {
                    "${ComponentStyles.getClassName { ComponentStyles::fileListItem }} ${ComponentStyles.getClassName { ComponentStyles::selectedFileListItem }}"
                } else {
                    ComponentStyles.getClassName { ComponentStyles::fileListItem }
                }
                key = assignedShortcut
            }
            styledDiv {
                css {
                    display = Display.block
                    minWidth = 75.pct
                }
                fileItem {
                    fileDiff = currentFileDiff
                    isSelected = currentFileDiff.isGivenFilePresentlySelected()
                }
            }
            styledDiv {
                css {
                    marginRight = 36.px
                }
                keyboardChip {
                    this.attrs {
                        onSelected = handlerForFile
                        this.assignedShortcut = assignedShortcut
                        uponUnmount = removePrefixOnUnmount
                    }
                }
            }
        }
    }

    private fun FileDiffV2.isGivenFilePresentlySelected() =
        props.selectedFile?.getUniqueIdentifier() == this.getUniqueIdentifier()

    private val removePrefixOnUnmount : (String) -> Unit = { _ ->
        // No-op
    }

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


external interface FileItemProps: RProps {
    var fileDiff: FileDiffV2
    var isSelected: Boolean
}

private class FileItem: RComponent<FileItemProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                margin(all = 0.px)
            }
            styledP {
                css {
                    margin(all = 0.px)
                    paddingTop = 16.px
                    paddingBottom = 16.px
                    alignContent = Align.baseline
                    fontSize = FontSizes.large
                    lineHeight = LineHeights.large
                    fontWeight = FontWeight.normal
                }
                styledSpan {
                    css {
                        color = if (props.isSelected) {
                            Colors.primaryTeal
                        } else {
                            Colors.textDarkGrey
                        }
                        classes.add(ComponentStyles.getClassName { ComponentStyles::fileListTshirtSizePosition })
                    }
                    + props.fileDiff.tShirtSize.name
                }
                styledSpan {
                    css {
                        minWidth = 70.pct
                        color = Colors.textMediumGrey
                        marginLeft = 40.px
                    }
                    + (props.fileDiff.newFile?.path ?: (props.fileDiff.oldFile!!.path)).split("/").last()
                }
            }
        }
    }

}

private fun RBuilder.fileItem(handler: FileItemProps.() -> Unit): ReactElement {
    return child(FileItem::class) {
        this.attrs(handler)
    }
}
