package supercr.workflows.codereview.components

import DraggableProvided
import ListItem
import codereview.FileDiffV2
import kotlinext.js.Object
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.FontWeight
import kotlinx.css.alignContent
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.lineHeight
import kotlinx.css.margin
import kotlinx.css.marginRight
import kotlinx.css.minWidth
import kotlinx.css.paddingBottom
import kotlinx.css.paddingTop
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.html.attributesMapOf
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLUListElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.createRef
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.FontSizes
import supercr.css.LineHeights
import supercr.kb.components.keyboardChip

external interface FileItemProps: RProps {
    var fileDiff: FileDiffV2
    var isSelected: Boolean
    var handlerForFile: () -> Unit
    var assignedShortcut: String
    var providedDraggable: DraggableProvided?
}

class FileItem: RComponent<FileItemProps, RState>() {
    override fun RBuilder.render() {
        ListItem {
            attrs {
                divider = false
                onClick = props.handlerForFile
                className = if (props.isSelected) {
                    "${ComponentStyles.getClassName { ComponentStyles::fileListItem }} ${ComponentStyles.getClassName { ComponentStyles::selectedFileListItem }}"
                } else {
                    ComponentStyles.getClassName { ComponentStyles::fileListItem }
                }
                key = props.assignedShortcut
                ref { r ->
                    props.providedDraggable?.innerRef?.invoke(r)
                }
                if (props.providedDraggable != null) {
                    Object.keys(props.providedDraggable!!.draggableProps).forEach { key ->
                        attrs.asDynamic()[key] = props.providedDraggable!!.draggableProps.asDynamic()[key]
                    }
                    Object.keys(props.providedDraggable!!.dragHandleProps).forEach { key ->
                        attrs.asDynamic()[key] = props.providedDraggable!!.dragHandleProps.asDynamic()[key]
                    }
                }
            }
            styledDiv {
                css {
                    display = Display.block
                    minWidth = 75.pct
                }
                renderFileName()
                attrs {
                }
            }
            renderKeyboardShortcut()
        }
    }

    private fun RBuilder.renderFileName() {
        styledDiv {
            css {
                margin(all = 0.px)
            }
            styledP {
                css {
                    margin(all = 0.px)
                    paddingTop = 18.px
                    paddingBottom = 18.px
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
                    +props.fileDiff.tShirtSize.name
                }
                styledSpan {
                    css {
                        minWidth = 70.pct
                        color = Colors.textMediumGrey
//                        marginLeft = 40.px
                    }
                    +(props.fileDiff.newFile?.path ?: (props.fileDiff.oldFile!!.path)).split("/").last()
                }
            }
        }
    }

    private fun RBuilder.renderKeyboardShortcut() {
        styledDiv {
            css {
                marginRight = 36.px
            }
            keyboardChip {
                this.attrs {
                    onSelected = props.handlerForFile
                    assignedShortcut = props.assignedShortcut
                    uponUnmount = removePrefixOnUnmount
                }
            }
        }
    }

    private val removePrefixOnUnmount : (String) -> Unit = { _ ->
        // No-op
    }
}

fun RBuilder.fileItem(handler: FileItemProps.() -> Unit): ReactElement {
    return child(FileItem::class) {
        this.attrs(handler)
    }
}
