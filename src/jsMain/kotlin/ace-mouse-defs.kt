@file:JsModule("ace-builds/src-noconflict/ace/mouse")

import kotlinext.js.Object
import org.w3c.dom.events.MouseEvent


@JsName("MouseEvent")
external class MouseEvent{
    val domEvent: MouseEvent
    val editor: dynamic

    /** 'row' and 'column' of the document position */
    fun getDocumentPosition(): RowColObject

    fun stopPropagation()

    fun preventDefault()

    /** Invokes both [stopPropagation] and [preventDefault] on the underlying [domEvent] */
    fun stop()
}

external class RowColObject{
    val row: Number
    val column: Number
}
