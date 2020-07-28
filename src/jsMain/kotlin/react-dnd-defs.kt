@file:JsModule("react-beautiful-dnd")

import kotlinext.js.Object
import org.w3c.dom.HTMLElement
import react.RClass
import react.RProps
import react.RReadableRef
import react.ReactElement

@JsName("DragDropContext")
external val DragDropContext: RClass<DragDropContextProps>

external interface DragDropContextProps: RProps {
    /** Read out by screen readers when focusing on a drag handle */
    var dragHandleUsageInstructions: String?
    /** Used for strict content access policies */
    var nonce: String?

    var onBeforeCapture: () -> Unit

    var onBeforeDragStart: () -> Unit
    var onDragStart: () -> Unit
    var onDragUpdate: () -> Unit
    var onDragEnd: (DropResult) -> Unit
}

external class DragStart {
    val draggableId: String
    val typeId: String
    val source: DraggableLocation
    /** FLUID or SNAP */
    val mode: String
}

external class DraggableLocation {
    val droppableId: String
    val index: Number
}
external class Combine {
    val draggableId: String
    val droppableId: String
}

external class DragUpdate {
    val draggableId: String
    val typeId: String
    val source: DraggableLocation
    /** FLUID or SNAP */
    val mode: String
    val destination: DraggableLocation?
    val combine: Combine?
}

external class DropResult {
    val draggableId: String
    val typeId: String
    val source: DraggableLocation
    /** FLUID or SNAP */
    val mode: String
    val destination: DraggableLocation?
    val combine: Combine?
    /** DROP or CANCEL */
    val reason: String
}

@JsName("Droppable")
external val Droppable: RClass<DroppableProps>

external interface DroppableProps: RProps {
    /** Required */
    var droppableId: String

    /**
     * A TypeId(string) that can be used to simply accept only the specified class of <Draggable />.
     * <Draggable />s always inherit type from the <Droppable /> they are defined in.
     * For example, if you use the type PERSON then it will only allow <Draggable />s of type PERSON
     * to be dropped on itself.
     * <Draggable />s of type TASK would not be able to be dropped on a <Droppable /> with type PERSON.
     * If no type is provided, it will be set to 'DEFAULT'.
     */
    var type: TypeId?
    /** standard | virtual */
    var mode: String

    /**
     * A flag to control whether or not dropping is currently allowed on the <Droppable />.
     * You can use this to implement your own conditional dropping logic.
     * It will default to false.
     */
    var isDropDisabled: Boolean

    /**
     * A flag to control whether or not all the Draggables in the list will be able to be combined with.
     * It will default to false.
     */
    var isCombineEnabled: Boolean
    /** horizontal | vertical */
    var direction: String

    /**
     * When a <Droppable /> is inside a scrollable container its area is constrained so that you can only drop
     * on the part of the <Droppable /> that you can see.
     * Setting this prop opts out of this behavior, allowing you to drop anywhere on a <Droppable /> even if it's
     * visually hidden by a scrollable parent.
     * The default behavior is suitable for most cases so odds are you'll never need to use this prop,
     * but it can be useful if you've got very long <Draggable />s inside a short scroll container.
     * Keep in mind that it might cause some unexpected behavior if you have multiple <Droppable />s inside
     * scroll containers on the same page.
     */
    var ignoreContainerClipping: Boolean

    /**
     * a function that returns the containing element (parent element) for a clone during a drag.
     */
    var getContainerForClone: () -> HTMLElement

    var children: (DroppableProvided, DroppableStateSnapshot) -> ReactElement
}

@JsName("TypeId")
external class TypeId(id: String)

@JsName("DroppableProvided")
external class DroppableProvided {
    val innerRef: (dynamic) -> Unit
    val droppableProps: RProps
    val placeholder: ReactElement
}

@JsName("DroppableStateSnapshot")
external class DroppableStateSnapshot {
    val isDraggingOver: Boolean
    /** Draggable id */
    val draggingOverWith: String
    val draggingFromThisWith: String
    val isUsingPlaceholder: Boolean
}

@JsName("Draggable")
external val Draggable: RClass<DraggableProps>

external interface DraggableProps: RProps {
    var draggableId: String
    var index: Number
    var isDragDisabled: Boolean

    /**
     * opt out of blocking a drag from interactive elements.
     */
    var disableInteractiveElementBlocking: Boolean

    /**
     * Whether or not the drag handle should respect force press interactions.
     */
    var shouldRespectForcePress: Boolean

    var children: (DraggableProvided, DraggableStateSnapshot) -> ReactElement

    var key: String
}

@JsName("DraggableProvided")
external class DraggableProvided {
    val innerRef: ( HTMLElement? ) -> Unit
    val draggableProps: RProps
    /** will be null if the draggable is disabled */
    val dragHandleProps: RProps
}

@JsName("DraggableStateSnapshot")
external class DraggableStateSnapshot {
    val isDragging: Boolean
    val isDropAnimating: Boolean
    /** Droppable id */
    val draggingOver: String?
    /** Draggable id */
    val combineWith: String?
    /** Draggable Id */
    val combineTargetFor: String?
    /** FLUID or SNAP */
    val mode: String
}

