package supercr.workflows.codereview.components

import Grid
import Paper
import codereview.DiffChangeType
import codereview.FileDiff
import kotlinx.css.minHeight
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.p
import styled.css
import styled.getClassName
import styled.styledDiv
import supercr.css.ComponentStyles
import supercr.css.TextStyles

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
                    md = 12
                }
                styledDiv {
                    css {
                        minHeight = 20.px
                    }
                }
            }
            renderDiffView()
        }
    }

    private fun RBuilder.renderDiffView() : ReactElement {
        return when(props.fileDiff.diffChangeType) {
            DiffChangeType.MODIFY -> renderDiffViewForModifiedFile()
            DiffChangeType.COPY -> TODO()
            DiffChangeType.RENAME -> renderDiffViewForRenamedFile()
            DiffChangeType.DELETE -> renderViewForRemovedFile()
            DiffChangeType.ADD -> renderViewForNewFile()
        }
    }

    private fun RBuilder.renderViewForNewFile(): ReactElement {
        return Grid {
            attrs {
                item = true
                container = false
                md = 12
            }
            Paper {
                attrs {
                    square = true
                    variant = "outlined"
                    className = ComponentStyles.getClassName { ComponentStyles::infoPaper }
                }
                p {
                    +props.fileDiff.fileHeader.fileNewPath
                }
            }
            Grid {
                attrs {
                    container = true
                    item = false
                    justify = "space-evenly"
                    spacing = 2
                }
                renderAceEditorForSingleFiles(props.fileDiff.rawTextNew ?: "", xsValToUse = 12, classNameToUse = TextStyles.insertedTextNew)
            }
        }
    }

    private fun RBuilder.renderViewForRemovedFile(): ReactElement {
        return Grid {
            attrs {
                item = true
                container = false
                md = 12
            }
            Paper {
                attrs {
                    square = true
                    variant = "outlined"
                    className = ComponentStyles.getClassName { ComponentStyles::infoPaper }
                }
                p {
                    +props.fileDiff.fileHeader.fileOldPath
                }
            }
            Grid {
                attrs {
                    container = true
                    item = false
                    justify = "space-evenly"
                    spacing = 2
                }
                renderAceEditorForSingleFiles(props.fileDiff.rawTextOld ?: "", xsValToUse = 12, classNameToUse = TextStyles.removedText)
            }
        }
    }

    private fun RBuilder.renderAceEditorForSingleFiles(text: String, xsValToUse: Number, classNameToUse: String): ReactElement {
        return codeView {
            id = props.fileDiff.fileHeader.identifier
            codeText = text
            className = classNameToUse
            xsValueToUse = xsValToUse
        }
    }

    private fun RBuilder.renderDiffViewForRenamedFile(): ReactElement {
        return Grid {
            attrs {
                container = true
                md = 12
                spacing = 0
                direction = "row"
                alignItems = "center"
                justify = "space-evenly"
            }
            Grid {
                attrs {
                    md = 6
                }
                Paper {
                    attrs {
                        square = true
                        variant = "outlined"
                        className = ComponentStyles.getClassName { ComponentStyles::infoPaper }
                    }
                    p {
                        + "${ props.fileDiff.fileHeader.fileOldPath } -> "
                    }
                }
            }
            Grid {
                attrs {
                    md = 6
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
            renderDiffPane()
        }
    }

    private fun RBuilder.renderDiffViewForModifiedFile(): ReactElement {
        return Grid {
            attrs {
                item = true
                container = false
                md = 12
            }
            Paper {
                attrs {
                    square = true
                    variant = "outlined"
                    className = ComponentStyles.getClassName { ComponentStyles::infoPaper }
                }
                p {
                    + props.fileDiff.fileHeader.fileNewPath
                }
            }
            renderDiffPane()
        }
    }

    /**
     * Renders a [DiffView] in a [Grid] item with the given xs value
      */
    private fun RBuilder.renderDiffPane(xsValToUse: Number = 12) : ReactElement {
        return Grid {
            attrs {
                item = true
                container = false
                md = xsValToUse
            }
            diffView {
                oldText = props.fileDiff.rawTextOld?:""
                newText = props.fileDiff.rawTextNew?:""
                editList = props.fileDiff.fileHeader.editList
                identifier = props.fileDiff.fileHeader.identifier
            }
        }
    }
}
fun RBuilder.fileView(handler: FileViewProps.() -> Unit): ReactElement {
    return child(FileView::class) {
        this.attrs(handler)
    }
}
