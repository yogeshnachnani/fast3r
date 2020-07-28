package supercr.workflows.codereview.components

import DragDropContext
import Draggable
import DraggableProvided
import DraggableStateSnapshot
import DropResult
import Droppable
import DroppableProvided
import DroppableStateSnapshot
import MaterialUIList
import codereview.FileDiffV2
import kotlinext.js.Object
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
import supercr.css.ComponentStyles

external interface DragNDropEnabledFileListProps : RProps {
    var fileList: List<FileDiffAndShortcut>
    var handleReordering: (Int, Int) -> Unit
}

external interface DragNDropEnabledFileListState : RState {
}

data class FileDiffAndShortcut(
    val fileDiffV2: FileDiffV2,
    val kbShortcut: String,
    val handlerForFile: () -> Unit
)

class DragNDropEnabledFileList constructor(
    constructorProps: DragNDropEnabledFileListProps
) : RComponent<DragNDropEnabledFileListProps, DragNDropEnabledFileListState>(constructorProps) {

    override fun RBuilder.render() {
        DragDropContext {
            attrs {
                onDragEnd = handleDragEnd
            }
            Droppable {
                attrs {
                    droppableId = "orderable-file-list"
                    children = buildFileListElements
                }
            }
        }
    }

    private val buildFileListElements: (provided: DroppableProvided, snapshot: DroppableStateSnapshot) -> ReactElement = { provided, snapshot ->
        buildElement {
            if (props.fileList.isNotEmpty()) {
                styledDiv {
                    css {
                        +ComponentStyles.fileListPane
                    }
                    /**
                     * The tutorial https://egghead.io/lessons/react-reorder-a-list-with-react-beautiful-dnd
                     * says that [ provided.droppableProps ] need to be added as props to the child component
                     */
                    attrs {
                        Object.keys(provided.droppableProps).forEach { key ->
                            this.attributes[key] = provided.droppableProps.asDynamic()[key]
                        }
                    }
                    ref {
                        provided.innerRef(it)
                    }
                    MaterialUIList {
                        attrs {
                            className = ComponentStyles.getClassName { ComponentStyles::fileList }
                        }
                        props.fileList
                            .mapIndexed { fileIndex, fileDiffAndShortcut ->
                                Draggable {
                                    attrs {
                                        key = fileDiffAndShortcut.kbShortcut
                                        draggableId = fileDiffAndShortcut.kbShortcut
                                        index = fileIndex.toDouble()
                                        children = fileDiffAndShortcut.makeFileListItemFn()
                                    }
                                }
                            }
                    }
                    /**
                     * The tutorial also mentions that this needs to be kept as one of the children of the main Droppable designate component
                     */
                    child(provided.placeholder)
                }
            }
        }
    }

    private fun FileDiffAndShortcut.makeFileListItemFn(): (DraggableProvided, DraggableStateSnapshot) -> ReactElement {
        return { draggableProvided, draggableStateSnapshot ->
            buildElement {
                fileItem {
                    fileDiff = this@makeFileListItemFn.fileDiffV2
                    isSelected = false
                    handlerForFile = this@makeFileListItemFn.handlerForFile
                    assignedShortcut = this@makeFileListItemFn.kbShortcut
                    providedDraggable = draggableProvided
                }
            }
        }
    }

    private val handleDragEnd : (DropResult) -> Unit = { result ->
        if (result.destination == null || result.source.index === result.destination.index) {
            // Nothing to do
        } else {
            props.handleReordering(result.source.index.toInt(), result.destination.index.toInt())
        }
    }
}

fun RBuilder.dndFileList(handler: DragNDropEnabledFileListProps.() -> Unit): ReactElement {
    return child(DragNDropEnabledFileList::class) {
        this.attrs(handler)
    }
}