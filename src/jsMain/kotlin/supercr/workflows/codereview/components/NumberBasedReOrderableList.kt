package supercr.workflows.codereview.components

import DragDropContext
import Draggable
import DraggableProvided
import DraggableStateSnapshot
import DropResult
import Droppable
import DroppableProvided
import DroppableStateSnapshot
import ListItem
import MaterialUIList
import kotlinext.js.Object
import kotlinx.css.basis
import kotlinx.css.flexBasis
import kotlinx.css.pct
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.buildElement
import react.key
import styled.css
import styled.getClassName
import styled.styledDiv
import supercr.css.ComponentStyles
import supercr.kb.components.keyboardChip

external interface NumberBasedReOrderableListProps : RProps {
    var children: List<() -> ReactElement>
    var itemClazz : String
    var handleReorderingFn: (Int, Int) -> Unit
    var listClazz: String
}

external interface NumberBasedReOrderableListState : RState {

}

class NumberBasedReOrderableList : RComponent<NumberBasedReOrderableListProps, NumberBasedReOrderableListState>() {
    override fun RBuilder.render() {
        DragDropContext {
            attrs {
                onDragEnd = handleDragEnd
            }
            Droppable {
                attrs {
                    droppableId = "orderable-file-list"
                    children = buildReOrderAbleList
                }
            }
        }
    }

    private val buildReOrderAbleList: (provided: DroppableProvided, snapshot: DroppableStateSnapshot) -> ReactElement = { provided, snapshot ->
        buildElement {
            if (props.children.isNotEmpty()) {
                styledDiv {
                    /**
                     * We've used [provided] within MaterialUIList itself. Alternatively, it could have been used under [this] styledDiv as below:
                     *
                     *      attrs {
                     *          Object.keys(provided.droppableProps).forEach { key ->
                     *              this.attributes[key] = provided.droppableProps.asDynamic()[key]
                     *          }
                     *      }
                     *      ref {
                     *          provided.innerRef(it)
                     *      }
                     */
                    MaterialUIList {
                        ref {
                            provided.innerRef(it)
                        }
                        attrs {
                            className = props.listClazz
                            /**
                             * The tutorial https://egghead.io/lessons/react-reorder-a-list-with-react-beautiful-dnd
                             * says that [ provided.droppableProps ] need to be added as props to the child component
                             */
                            Object.keys(provided.droppableProps).forEach { key ->
                                attrs.asDynamic()[key] = provided.droppableProps.asDynamic()[key]
                            }
                        }
                        props.children.mapIndexed { childIndex, childRenderfn ->
                            Draggable {
                                attrs {
                                    key = childIndex.kbShortcutFromIndex()
                                    draggableId = childIndex.kbShortcutFromIndex()
                                    index = childIndex.toDouble()
                                    children = childRenderfn.makeListItem()
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

    private fun (() -> ReactElement).makeListItem(): (DraggableProvided, DraggableStateSnapshot) -> ReactElement {
        return { draggableProvided, draggableStateSnapshot ->
            buildElement {
                ListItem {
                    attrs {
                        className = props.itemClazz
                        key = draggableProvided.draggableProps.asDynamic()["data-rbd-draggable-id"] as String
                        ref { r ->
                            draggableProvided.innerRef.invoke(r)
                        }
                        Object.keys(draggableProvided.draggableProps).forEach { key ->
                            attrs.asDynamic()[key] = draggableProvided.draggableProps.asDynamic()[key]
                        }
                        Object.keys(draggableProvided.dragHandleProps).forEach { key ->
                            attrs.asDynamic()[key] = draggableProvided.dragHandleProps.asDynamic()[key]
                        }
                    }
                    styledDiv {
                        css {
                            flexBasis = 85.pct.basis
                        }
                        child(this@makeListItem.invoke())
                    }
                    styledDiv {
                        css {
                            flexBasis = 15.pct.basis
                        }
                        /** Commenting this instead of removing since we'll be moving to new git project soon and the history would be lost (TODO: Delete once we move to new project) */
//                        keyboardChip {
//                            attrs {
//                                onSelected = {
//
//                                }
//                                assignedShortcut = draggableProvided.draggableProps.asDynamic()["data-rbd-draggable-id"] as String
//                                uponUnmount = handleShortcutUnmount
//                                className = ComponentStyles.getClassName { ComponentStyles::numberedListKeyboardShortcut }
//                            }
//                        }
                    }
                }
            }
        }
    }

    private fun Int.kbShortcutFromIndex(): String {
        return if (props.children.size > 9 && this <= 9) {
            "0$this"
        } else {
            this.toString()
        }
    }

    private val handleShortcutUnmount: (String) -> Unit = { _ ->
        //no op
    }
    private val handleDragEnd : (DropResult) -> Unit = { result ->
        if (result.destination == null || result.source.index === result.destination.index) {
            // Nothing to do
        } else {
            props.handleReorderingFn(result.source.index.toInt(), result.destination.index.toInt())
        }
    }
}

fun RBuilder.numberBasedReOrderableList(handler: NumberBasedReOrderableListProps.() -> Unit): ReactElement {
    return child(NumberBasedReOrderableList::class) {
        this.attrs(handler)
    }
}