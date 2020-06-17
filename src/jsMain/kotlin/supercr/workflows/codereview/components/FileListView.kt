package supercr.workflows.codereview.components

import Grid
import ListItem
import ListSubHeader
import MaterialUIList
import codereview.FileDiff
import codereview.FileDiffList
import datastructures.KeyboardShortcutTrie
import kotlinx.css.color
import kotlinx.css.em
import kotlinx.css.marginBottom
import kotlinx.css.marginLeft
import kotlinx.css.marginTop
import kotlinx.css.px
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
import supercr.components.fileSizeChip
import supercr.css.AvatarSize
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.kb.components.keyboardEnabledComponent

enum class FileDiffListMode {
    compact,
    expanded
}

/**
 * Displays the list of files given by [fileList] prop.
 * Also enables keyboard access for each individual file
 * This Component manages the order in which the list of files are displayed.
 * Since this component 'owns' the list of files and the order in which they're displayed, it acts as the primary
 * control for the flow of the code review.
 * It provides callbacks for various events that occur during
 */
external interface FileListViewProps : RProps{
    var fileList: FileDiffList
    var mode: FileDiffListMode
    var onFileSelect: (FileDiff?) -> Unit
    var onFileMarkedDone: (FileDiff) -> Unit
    var onFileMarkedForLater: (FileDiff) -> Unit
    var onPreviousFile: (FileDiff, FileDiff) -> Unit
    var onAllDone: () -> Unit
}

enum class FileReviewStatus {
    TO_BE_REVIEWED,
    REVIEWED,
    SAVED_FOR_LATER
}

external interface FileListViewState: RState {
    var selectedFile: FileDiff?
    var fileDiffShortutAndStatusList: List<Triple<FileDiff, String, FileReviewStatus>>
}

class FileListView: RComponent<FileListViewProps, FileListViewState>() {
    override fun FileListViewState.init() {
        fileDiffShortutAndStatusList = listOf()
    }

    override fun RBuilder.render() {
        if (state.fileDiffShortutAndStatusList.isNotEmpty()) {
            MaterialUIList {
                ListSubHeader {
                    attrs {
                        disableGutters = true
                        inset = false
                    }
                    + "Files"
                }
            }
            listOf(FileReviewStatus.TO_BE_REVIEWED, FileReviewStatus.SAVED_FOR_LATER, FileReviewStatus.REVIEWED)
                .map { reviewStatus ->
                    renderListSeparator(reviewStatus)
                    state.fileDiffShortutAndStatusList
                        .filter { it.third == reviewStatus}
                        .map { (currentFileDiff, assignedShortcut) ->
                            renderFileItem(currentFileDiff, assignedShortcut)
                        }
                }
        }
    }
    private fun RBuilder.renderListSeparator(fileReviewStatus: FileReviewStatus) {
        ListItem {
            attrs {
                divider = true
                className = "${ComponentStyles.getClassName { ComponentStyles::compactFileListItem }}"
            }
            styledP {
                css {
                    marginTop = 0.10.em
                    marginBottom = 0.px
                    color = Colors.warmGreyBase
                }
                + fileReviewStatus.displayText()
            }
        }
    }

    private fun RBuilder.renderFileItem(currentFileDiff: FileDiff, assignedShortcut: String) {
        val handlerForFile = createHandlerFor(currentFileDiff)
        ListItem {
            attrs {
                divider = false
                onClick = handlerForFile
                className = ComponentStyles.getClassName { ComponentStyles::compactFileListItem }
            }
            keyboardEnabledComponent {
                elementToRender = buildElement {
                    fileItem {
                        fileDiff = currentFileDiff
                    }
                }!!
                onSelected = handlerForFile
                this.assignedShortcut = assignedShortcut
                uponUnmount = removePrefixOnUnmount
            }
        }
    }

    override fun componentDidMount() {
        val generatedPrefixes = KeyboardShortcutTrie.generatePossiblePrefixCombos(
            prefixString = null,
            numberOfComponents = props.fileList.fileDiffs.size
        ).toList()
        generatedPrefixes.mapIndexed { index, prefix ->
            Triple(props.fileList.fileDiffs[index], prefix, FileReviewStatus.TO_BE_REVIEWED)
        }.also {
            setState {
                fileDiffShortutAndStatusList = it
            }
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
            props.onFileSelect(currentFileDiff)
        }
    }
    private val removePrefixOnUnmount : (String) -> Unit = { prefixToRemove ->
        val isPrefixPresent = state.fileDiffShortutAndStatusList.any { it.second == prefixToRemove }
        if (isPrefixPresent) {
            val newList = state.fileDiffShortutAndStatusList.filterNot {
                it.second == prefixToRemove
            }
            setState {
                fileDiffShortutAndStatusList = newList
            }
        }
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
        styledP {
            css {
                marginTop = 0.10.em
                marginBottom = 0.px
                color = Colors.baseText1
            }
            + props.fileDiff.fileHeader.fileNewPath.split("/").last()
        }
        styledDiv {
            css {
                marginTop = 0.px
                marginLeft = 8.px
                color = Colors.baseText
            }
            fileSizeChip {
                fileSize = props.fileDiff.fileHeader.tShirtSize
                avatarSize = AvatarSize.tiny
            }
        }
    }

    fun RBuilder.oldrender() {
        Grid {
            attrs {
                item = false
                container = true
                direction = "row"
                justify = "flex-start"
                alignItems = "center"
                spacing = 1
                className = ComponentStyles.getClassName { ComponentStyles::fileItemText }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                }
                fileSizeChip {
                    fileSize = props.fileDiff.fileHeader.tShirtSize
                    avatarSize = AvatarSize.tiny
                }
            }
            Grid{
                attrs {
                    item = true
                    container = false
                }
                + props.fileDiff.fileHeader.fileNewPath.split("/").last()
            }
        }
    }

}

private fun RBuilder.fileItem(handler: FileItemProps.() -> Unit): ReactElement {
    return child(FileItem::class) {
        this.attrs(handler)
    }
}
