@file:JsModule("ace-builds/src-noconflict/ace")

import org.w3c.dom.HTMLDivElement

@JsName("Range")
external class Range(
    startRow: Number,
    startColumn: Number,
    endRow: Number,
    endColumn: Number
)

@JsName("Editor")
external class Editor {
    val renderer: VirtualRenderer
    val container: HTMLDivElement

    fun getSession(): EditSession
    fun setSession(editSession: EditSession)

    fun scrollToLine(lineNumber: Number, center: Boolean, animate: Boolean)

    /**
     * Moves the cursor to the specified line number, and also into the indicated column.
     */
    fun gotoLine(lineNumber: Number, column: Number, animate: Boolean)

    fun on(eventName: String, callback: Any)

    fun resize(force: Boolean)

    fun isFocused(): Boolean

    fun focus(): Unit
}
@JsName("EditSession")
external class EditSession(
    text: String,
    textMode: String
) {
    fun addGutterDecoration(row: Number, className: String)
    fun setScrollTop(scrollTop: Number)
    fun on(eventName: String, callback: Any)
    fun addMarker(range: Range, cssClassName: String, type: String, inFront: Boolean)
    fun getLine(row: Number): String
    fun highlightLines(fromRow: Number, endRow: Number, className: String, inFront: Boolean = definedExternally)
}

@JsName("VirtualRenderer")
external class VirtualRenderer {
    val scrollBarV: VScrollBar
    fun textToScreenCoordinates(row: Number, column: Number): ScreenCoordinates
}

external class ScreenCoordinates{
    val pageX: Number
    val pageY: Number
}

@JsName("VScrollBar")
external class VScrollBar {
    val element: HTMLDivElement
}