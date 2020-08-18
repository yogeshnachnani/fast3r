package supercr.workflows.codereview.components

import BookmarkBorder
import CheckBoxOutlined
import Done
import DraggableProvided
import ListItem
import NotificationsNone
import codereview.FileDiffV2
import kotlinext.js.Object
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.margin
import kotlinx.css.minWidth
import kotlinx.css.pct
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.EditorThemeColors
import supercr.kb.components.keyboardChip

external interface FileItemProps: RProps {
    var fileDiff: FileDiffV2
    var isSelected: Boolean
    var handlerForFile: () -> Unit
    var assignedShortcut: String
    var providedDraggable: DraggableProvided?
    var reviewStatus: FileReviewStatus
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
                    + ComponentStyles.fileListItemContainer
                }
                styledSpan {
                    css {
                        color = getIconColor()
                        classes.add(ComponentStyles.getClassName { ComponentStyles::fileListTshirtSizePosition })
                    }
                    getStatusItem()
                }
                styledSpan {
                    css {
                        minWidth = 70.pct
                        color = Colors.textMediumGrey
                    }
                    +(props.fileDiff.newFile?.path ?: (props.fileDiff.oldFile!!.path)).split("/").last()
                }
            }
        }
    }

    private fun RBuilder.renderKeyboardShortcut() {
        styledDiv {
            css {
                + ComponentStyles.fileListItemKeyboardShortcutContainer
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

    private fun getIconColor(): Color {
        return if (props.isSelected) {
            Colors.primaryTeal
        } else {
            when(props.reviewStatus) {
                FileReviewStatus.TO_BE_REVIEWED -> Colors.textDarkGrey
                FileReviewStatus.REVIEWED -> EditorThemeColors.tokenLightBlue
                FileReviewStatus.SAVED_FOR_LATER -> EditorThemeColors.tokenLightOrange
            }
        }
    }

    private fun RBuilder.getStatusItem() {
        when(props.reviewStatus) {
            FileReviewStatus.TO_BE_REVIEWED -> + props.fileDiff.tShirtSize.name
            FileReviewStatus.REVIEWED -> Done {
                attrs { fontSize = "1.25em" }
            }
            FileReviewStatus.SAVED_FOR_LATER -> BookmarkBorder {
                attrs { fontSize = "1.25em" }
            }
        }
    }
}

fun RBuilder.fileItem(handler: FileItemProps.() -> Unit): ReactElement {
    return child(FileItem::class) {
        this.attrs(handler)
    }
}
