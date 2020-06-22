package supercr.workflows.codereview.components

import Grid
import ListItem
import ListSubHeader
import MaterialUIList
import codereview.FileDiff
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.Float
import kotlinx.css.LinearDimension
import kotlinx.css.alignContent
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.em
import kotlinx.css.float
import kotlinx.css.fontSize
import kotlinx.css.margin
import kotlinx.css.marginBottom
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.maxWidth
import kotlinx.css.minHeight
import kotlinx.css.minWidth
import kotlinx.css.pc
import kotlinx.css.pct
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.buildElement
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.components.fileSizeChip
import supercr.css.AvatarSize
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.FontSizes
import supercr.kb.components.keyboardChip

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
    var selectedFile: FileDiff?
}

enum class FileReviewStatus {
    TO_BE_REVIEWED,
    REVIEWED,
    SAVED_FOR_LATER
}

data class FileDiffStateAndMetaData(
    val fileDiff: FileDiff,
    val assignedShortcut: String,
    val currentStatus: FileReviewStatus,
    val handler: () -> Unit
)

external interface FileListViewState: RState {
}

class FileListView: RComponent<FileListViewProps, FileListViewState>() {

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
                        props.fileList
                            .filter { it.currentStatus == reviewStatus}
                            .map { (currentFileDiff, assignedShortcut, _, handler) ->
                                renderFileItem(currentFileDiff, assignedShortcut, handler)
                            }
                    }
            }
        }
    }

    private fun RBuilder.renderListSeparator(fileReviewStatus: FileReviewStatus) {
        ListItem {
            attrs {
                divider = true
                className = ComponentStyles.getClassName { ComponentStyles::compactFileListItem }
            }
            styledP {
                css {
                    marginTop = 8.px
                    marginBottom = 8.px
                    color = Colors.warmGreyBase
                }
                + fileReviewStatus.displayText()
            }
        }
    }

    private fun RBuilder.renderFileItem(currentFileDiff: FileDiff, assignedShortcut: String, handlerForFile: () -> Unit) {
        ListItem {
            attrs {
                divider = false
                onClick = handlerForFile
                className = ComponentStyles.getClassName { ComponentStyles::compactFileListItem }
                key = assignedShortcut
            }
            styledDiv {
                css {
                    display = Display.block
                    minWidth = 90.pct
                }
                fileItem {
                    fileDiff = currentFileDiff
                }
            }
            styledDiv {
                css {
                    maxWidth = 10.pct
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

}
fun RBuilder.fileList(handler: FileListViewProps.() -> Unit): ReactElement {
    return child(FileListView::class) {
        this.attrs(handler)
    }
}


external interface FileItemProps: RProps {
    var fileDiff: FileDiff
}

private class FileItem: RComponent<FileItemProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                marginTop = 8.px
                marginBottom = 0.px
            }
            styledP {
                css {
                    margin(all = 0.px)
                    color = Colors.baseText1
                    alignContent = Align.baseline
                }
                styledSpan {
                    css {
                        float = Float.left
                        color = Colors.warmGrey5
                        minWidth = 10.pct
                        fontSize = FontSizes.tiny
                        marginRight = 4.px
                    }
                    + props.fileDiff.fileHeader.tShirtSize.name
                }
                styledSpan {
                    css {
                        minWidth = 70.pct
                        fontSize = FontSizes.small
                    }
                    + props.fileDiff.fileHeader.fileNewPath.split("/").last()
                }
            }
        }
//        styledDiv {
//            css {
//                marginTop = 0.px
//                marginLeft = 8.px
//                color = Colors.baseText
//            }
//            fileSizeChip {
//                fileSize = props.fileDiff.fileHeader.tShirtSize
//                avatarSize = AvatarSize.tiny
//            }
//        }
    }

}

private fun RBuilder.fileItem(handler: FileItemProps.() -> Unit): ReactElement {
    return child(FileItem::class) {
        this.attrs(handler)
    }
}
