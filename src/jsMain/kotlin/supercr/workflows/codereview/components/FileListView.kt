package supercr.workflows.codereview.components

import Grid
import ListItem
import ListSubHeader
import codereview.FileDiff
import codereview.FileDiffList
import kotlinx.css.Display
import kotlinx.css.display
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
import supercr.css.ComponentStyles
import supercr.kb.components.keyboardEnabledList

enum class FileDiffListMode {
    compact,
    expanded
}

external interface FileListViewProps : RProps{
    var fileList: FileDiffList
    var mode: FileDiffListMode
    var onFileSelect: (FileDiff?) -> Unit
}

external interface FileListViewState: RState {
    var selectedIndex: Int?
}

class FileListView: RComponent<FileListViewProps, FileListViewState>() {
    override fun RBuilder.render() {
        keyboardEnabledList {
            elementsWithHandlers = createFileListElements()
            listSubHeader = buildElement {
                ListSubHeader {
                    + "Files"
                }
            }
            listItemClassName = ComponentStyles.getClassName { ComponentStyles::compactFileListItem }
        }
    }

    private fun createFileListElements(): List<Pair<ReactElement, () -> Unit>> {
        return props.fileList.fileDiffs.mapIndexed { index, currentFileDiff ->
            val element = buildElement {
                fileItem {
                    fileDiff = currentFileDiff
                }
            }
            val handler: () -> Unit = {
                handleClick(index).invoke()
            }
            Pair(element!!, handler)
        }
    }

    private fun handleClick(index: Int): () -> Unit {
        return {
            val (currentIndex, currentFileDiff) = if (state.selectedIndex != null && state.selectedIndex == index) {
                /** Basically toggling */
                Pair(null, null)
            } else {
                Pair(index, props.fileList.fileDiffs[index])
            }
            setState {
                selectedIndex = currentIndex
            }
            props.onFileSelect(currentFileDiff)
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
            }
            + props.fileDiff.fileHeader.fileNewPath.split("/").last()
        }
        styledP {
            css {
                marginTop = 0.px
                marginLeft = 8.px
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
