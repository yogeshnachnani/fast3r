package supercr.views

import Grid
import Paper
import codereview.FileDiff
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.p

/**
 * Show the diff/details for a file.
 * Depending on whether the file is new/modified/deleted, we can show it in a different way
 */
external interface FileViewProps: RProps {
    var fileDiff: FileDiff
}

external interface FileViewState: RState {

}

class FileView : RComponent<FileViewProps, FileViewState>() {
    override fun RBuilder.render() {
        Grid {
            attrs {
                item = false
                container = true
                alignItems = "center"
                direction = "row"
                justify = "center"
                spacing = 0
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    xs = 12
                }
                Paper {
                    attrs {
                        square = true
                        variant = "outlined"
                    }
                    p {
                        + props.fileDiff.fileHeader.fileNewPath
                    }
                }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    xs = 12
                }
                diffView {
                    oldText = props.fileDiff.rawTextOld?:""
                    newText = props.fileDiff.rawTextNew?:""
                    editList = props.fileDiff.fileHeader.editList
                }
            }
        }
    }

}
fun RBuilder.fileView(handler: FileViewProps.() -> Unit): ReactElement {
    return child(FileView::class) {
        this.attrs(handler)
    }
}
