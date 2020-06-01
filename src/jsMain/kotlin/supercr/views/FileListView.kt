package supercr.views

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

enum class FileDiffListMode {
    compact,
    expanded
}

external interface FileListViewProps : RProps{
    var fileList: FileDiffList
    var mode: FileDiffListMode
    var onFileSelect: (FileDiff) -> Unit
}

class FileListView: RComponent<FileListViewProps, RState>() {
    override fun RBuilder.render() {
        MaterialUIList {
            attrs {
                subHeader = ListSubHeader {
                    attrs {
                        inset = false
                    }
                    +   "Files"
                }
            }
            props.fileList.fileDiffs.map {
                ListItem {
                    attrs {
                        button = true
                        divider = true
                        onClick = handleClick(it)
                    }
                    fileItem {
                        fileDiff = it
                    }
                }
            }
        }
    }

    private fun handleClick(fileDiff: FileDiff): () -> Unit {
        return {
            props.onFileSelect(fileDiff)
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
