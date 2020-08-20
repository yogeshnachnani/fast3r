package supercr.kb

import datastructures.KeyboardShortcutTrie
import datastructures.PrefixMatchHandlers
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window

/**
 * A note on implementation:
 *
 * UniversalKeyboardHandler is a kotlin 'object' that maintains it's own
 * state and it is not a React component. Initially, I tried making it a
 * functional component(https://reactjs.org/docs/hooks-intro.html) but that
 * was proving too cumbersome.
 * For it to work seamlessly, it seems the 'functional' component would have
 * to end up being the 'parent' component for the entire screen and we
 * would have had to somehow keep passing methods to register shortcuts etc.
 * This is because unlike other React based shortcut handlers, the 'shortcuts'
 * for us are not static - pretty much calculated independently for every component/render
 * that needs it
 *
 * Leveraging the power of Kotlin seemed so much simpler, easier to
 * maintain and handle
 */

/**
 * Used to register a Keyboard shortcut (see [registerShortcut] method )
 * TODO: Write test cases before making further edits
 */

enum class DiffViewShortcuts(val label: String, val shortcutString: String) {
    NextFile("Next File", "fj"),
    ReviewLater("Review Later", "fl"),
    WindowOther("Swith Window", "wo"),
    PageDown("Page Down", "J"),
    PageUp("Page Up", "K")
}


object UniversalKeyboardShortcutHandler {

    /**
     * To be called at startup time
     */
    fun init() {
        if (!isInitialised) {
            console.log("Registering universal keyboard shortcut handler")
            window.addEventListener("keydown", keydownListener)
            isInitialised = true
        }
    }

    /**
     * @param shortcutString: Specify the shortcut string to be used. An exception is thrown if it is already assigned to something.
     * Use [KeyboardShortcutTrie] to get the shortcut strings
     * @param fullMatchHandler: Invoked when the shortcut is clicked. This should ideally be the same as the onClick handler of a component
     * @param partialMatchHandler: Invoked when part of the given [shortcutString] is selected by the user
     */
    fun registerShortcut(shortcutString: String, fullMatchHandler: ()->Unit, partialMatchHandler: (String) -> Unit) {
        val partialMatchCallback: () -> Unit = {
            partialMatchHandler(currentSelectedPrefix)
        }
        KeyboardShortcutTrie[shortcutString] = PrefixMatchHandlers(fullMatch = fullMatchHandler, partialMatchHandler = partialMatchCallback)
    }

    fun unRegisterShortcut(shortcutString: String) {
        KeyboardShortcutTrie.remove(shortcutString)
    }

    fun registerEnterKeyShortcut(handler: () -> Unit) {
        this.enterKeyHandler = handler
    }
    fun unregisterEnterKeyShortcut() {
        this.enterKeyHandler = null
    }
    fun registerCtrlEnterKeyShortcut(handler: () -> Unit) {
        this.ctrlEnterKeyHandler = handler
    }
    fun unregisterCtrlEnterKeyShortcut() {
        this.ctrlEnterKeyHandler = null
    }
    fun registerEscapeHandler(handler: ( () -> Unit )?) {
        this.escapeKeyHandler = handler
    }
    fun unregisterEscapeKeyShortcut() {
        this.escapeKeyHandler = null
    }
    fun disableShortcuts() {
        this.isDisabled = true
    }
    fun enableShortcuts() {
        this.isDisabled = false
    }

    private var currentSelectedPrefix = ""
    private var currentNumericPrefix = ""
    private val numericEndKeyAndHandlers: MutableMap<Char, (Int) -> Unit> = mutableMapOf()
    private var currentPartialMatches = mutableSetOf<() -> Unit>()
    private var isInitialised = false
    private var enterKeyHandler: ( () -> Unit )? = null
    private var escapeKeyHandler: ( () -> Unit )? = null
    private var ctrlEnterKeyHandler: ( () -> Unit )? = null
    private var isDisabled: Boolean = false

    private val keydownListener: (Event) -> Unit ={ keydownEvent ->
        val kbEvent = keydownEvent as KeyboardEvent
        when(kbEvent.key) {
            "Enter" -> {
                kbEvent.preventDefault()
                if (kbEvent.ctrlKey) {
                    ctrlEnterKeyHandler?.invoke()
                } else {
                    enterKeyHandler?.invoke()
                }
            }
            "Escape" -> {
                kbEvent.preventDefault()
                invokeAndClearSelection(escapeKeyHandler)
            }
            else -> {
                if (!this.isDisabled) {
                    if (keydownEvent.key.toIntOrNull() != null || currentNumericPrefix.isNotEmpty()) {
                        /** this is either a number, or the culmination of a numeric shortcut */
                        kbEvent.handleNumericShortcut()
                    }
                    handleCharBasedShortcut(kbEvent)
                }
            }
        }
    }

    private fun KeyboardEvent.handleNumericShortcut() {
        val number = this.key.toIntOrNull()
        if (number != null) {
            /** It's a number, so just keep adding to the prefix */
            currentNumericPrefix = "$currentNumericPrefix$number"
        } else {
            /** The shortcut handler must be invoked */
            val handler = numericEndKeyAndHandlers[this.key[0]]
            if (handler != null) {
                invokeNumericShortcutHandlerAndClearSelection(this, handler)
            } else {
                /** The user pressed a character that doesn't have a handler */
                invokeNumericShortcutHandlerAndClearSelection(this, null)
                escapeKeyHandler?.invoke()
            }
        }
    }

    private fun handleCharBasedShortcut(kbEvent: KeyboardEvent) {
        val newSelectedPrefix = "$currentSelectedPrefix${kbEvent.key}"
        KeyboardShortcutTrie[newSelectedPrefix]
            .also { (fullMatch, partialMatchHandlers) ->
                if (fullMatch == null && partialMatchHandlers.isEmpty()) {
                    /** We didn't hit anything */
                    invokeAndClearSelection(null)
                } else {
                    kbEvent.preventDefault()
                    if (fullMatch != null) {
                        invokeAndClearSelection(fullMatch)
                    } else {
                        setCurrentPrefix(newSelectedPrefix)
                        partialMatchHandlers.forEach {
                            it.invoke()
                            currentPartialMatches.add(it)
                        }
                    }
                }
            }
    }

    private val invokeNumericShortcutHandlerAndClearSelection: (KeyboardEvent, ( (Int) -> Unit )?) -> Unit = { keyboardEvent , handlerToInvoke ->
        val prefixAsNumber = currentNumericPrefix.toInt()
        if (handlerToInvoke != null) {
            keyboardEvent.preventDefault()
        }
        handlerToInvoke?.invoke(prefixAsNumber)
        currentNumericPrefix = ""
    }

    private val invokeAndClearSelection: (( ()->Unit )?) -> Unit = { handlerToInvoke ->
        setCurrentPrefix("")
        handlerToInvoke?.invoke()
        currentPartialMatches.forEach {
            it.invoke()
        }
        currentPartialMatches.clear()
    }

    private val setCurrentPrefix: (String) -> Unit = { prefixToSet ->
        currentSelectedPrefix = prefixToSet
    }

    /**
     * TODO: Hack introduced for running test cases
     */
    fun clear() {
        invokeAndClearSelection(null)
        this.enterKeyHandler = null
        this.escapeKeyHandler = null
        this.ctrlEnterKeyHandler = null
        this.isDisabled = false
        this.numericEndKeyAndHandlers.clear()
    }

    fun registerNumericEndKey(endChar: Char, handler: (Int) -> Unit) {
        numericEndKeyAndHandlers[endChar] = handler
    }
}

