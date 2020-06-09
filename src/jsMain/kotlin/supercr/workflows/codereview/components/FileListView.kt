package supercr.workflows.codereview.components

import Avatar
import Grid
import ListItem
import ListSubHeader
import MaterialUIList
import codereview.FileDiff
import codereview.FileDiffList
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.buildElement
import react.createElement
import react.setState
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
        }
    }

    private fun createFileListElements(): List<Pair<ReactElement, () -> Unit>> {
        return props.fileList.fileDiffs.mapIndexed { index, currentFileDiff ->
            val element = buildElement {
                ListItem {
                    attrs {
                        button = true
                        divider = true
                        onClick = handleClick(index)
                        selected
                    }
                    fileItem {
                        fileDiff = currentFileDiff
                    }
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
        Grid {
            attrs {
                item = false
                container = true
                direction = "row"
                justify = "space-between"
                alignItems = "center"
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    xs = 10
                }
//                    Paper {
//                    p {
                + props.fileDiff.fileHeader.fileNewPath.split("/").last()
//                    }
//                    }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    xs = 2
                }
                Avatar {
                    + "XS"
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
