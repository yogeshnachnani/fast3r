package supercr.workflows.codereview.components

import Grid
import Paper
import codereview.DiffChangeType
import codereview.FileDiffV2
import codereview.FilePatchType
import codereview.getUniqueIdentifier
import kotlinx.css.Align
import kotlinx.css.BoxSizing
import kotlinx.css.Display
import kotlinx.css.FlexWrap
import kotlinx.css.alignContent
import kotlinx.css.alignItems
import kotlinx.css.boxSizing
import kotlinx.css.display
import kotlinx.css.flexWrap
import kotlinx.css.height
import kotlinx.css.minHeight
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.p
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import supercr.css.ComponentStyles
import supercr.workflows.codereview.processor.FileDiffCommentHandler

/**
 * Show the diff/details for a file.
 * Depending on whether the file is new/modified/deleted, we can show it in a different way
 */
external interface FileViewProps: RProps {
    var fileDiff: FileDiffV2
    var fileDiffCommentHandler: FileDiffCommentHandler
    var defaultActionBarActions: List<ActionBarShortcut>
}

external interface FileViewState: RState {

}

class FileView : RComponent<FileViewProps, FileViewState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                + ComponentStyles.fileViewPane
            }
            renderFileHeader()
            renderDiffPane()
            renderExtraInfoWindow()
        }
    }

    private fun RBuilder.renderExtraInfoWindow() {
        extraInfoWindow {

        }
    }

    private fun RBuilder.renderFileHeader() {
        styledDiv {
            css {
                + ComponentStyles.fileViewFileInfo
            }
            styledDiv {
                css {
                    + ComponentStyles.fileViewFileInfoText
                }
                + when(props.fileDiff.diffChangeType) {
                    DiffChangeType.MODIFY -> ( props.fileDiff.newFile?.path ?: ( props.fileDiff.oldFile!!.path )  )
                    DiffChangeType.RENAME -> "${ props.fileDiff.oldFile!!.path } -> props.fileDiff.newFile!!.path"
                    DiffChangeType.DELETE -> props.fileDiff.oldFile!!.path
                    DiffChangeType.ADD ->  props.fileDiff.newFile!!.path
                    DiffChangeType.COPY -> TODO()
                }
                + when(props.fileDiff.patchType) {
                    FilePatchType.BINARY -> " (Binary)"
                    FilePatchType.TEXT -> ""
                }
            }
        }
    }

    /**
     * Renders a [DiffView] in a [Grid] item with the given xs value
      */
    private fun RBuilder.renderDiffPane() : ReactElement {
        return  diffView {
            fileDiff = props.fileDiff
            identifier = props.fileDiff.getUniqueIdentifier()
            oldFileNewCommentHandler = props.fileDiffCommentHandler.oldFileCommentHandler!!
            newFileNewCommentHandler = props.fileDiffCommentHandler.newFileCommentHandler!!
            defaultActionBarActions = props.defaultActionBarActions
        }
    }
}
fun RBuilder.fileView(handler: FileViewProps.() -> Unit): ReactElement {
    return child(FileView::class) {
        this.attrs(handler)
    }
}
