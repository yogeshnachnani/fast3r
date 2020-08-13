import datastructures.KeyboardShortcutTrie
import datastructures.isNoMatch
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.KeyboardEventInit
import supercr.kb.UniversalKeyboardShortcutHandler
import kotlin.browser.window
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UniversalKeyboardHandlerTest {

    @BeforeTest
    fun initHandler() {
        UniversalKeyboardShortcutHandler.clear()
        UniversalKeyboardShortcutHandler.init()
        KeyboardShortcutTrie.clear()
    }

    @Test
    fun shouldRegisterHandlerWithTrie() {
        val fullMatchHandler : () -> Unit = {

        }
        val partialMatchHandler : (String) -> Unit = {

        }
        UniversalKeyboardShortcutHandler.registerShortcut("foo", fullMatchHandler, partialMatchHandler)

        assertEquals(KeyboardShortcutTrie["foo"].fullMatch, fullMatchHandler)
    }

    @Test
    fun shouldUnregisterHandlerWithTrie() {
        val fullMatchHandler : () -> Unit = {

        }
        val partialMatchHandler : (String) -> Unit = {

        }
        UniversalKeyboardShortcutHandler.registerShortcut("foo", fullMatchHandler, partialMatchHandler)
        UniversalKeyboardShortcutHandler.unRegisterShortcut("foo")
        assertTrue(KeyboardShortcutTrie["foo"].isNoMatch())
    }

    @Test
    fun shouldHandleKeyPresses_invokePartialMatches(){
        var fullMatchInvoked = 0
        val fullMatchHandler : () -> Unit = {
            fullMatchInvoked++
        }
        var partialMatchInvoked = 0
        val partialMatchHandler : (String) -> Unit = {
            partialMatchInvoked++
        }
        UniversalKeyboardShortcutHandler.registerShortcut("foo", fullMatchHandler, partialMatchHandler)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("f")))
        assertEquals(1 ,partialMatchInvoked)
        assertEquals(0 ,fullMatchInvoked)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("o")))
        assertEquals(2 ,partialMatchInvoked)
        assertEquals(0 ,fullMatchInvoked)
    }

    @Test
    fun shouldHandleKeyPresses_resetOnEscape() {

        var fullMatchInvoked = 0
        val fullMatchHandler : () -> Unit = {
            fullMatchInvoked++
        }
        val matchedStringsList = mutableListOf<String>()
        val partialMatchHandler : (String) -> Unit = { matchedString ->
            matchedStringsList.add(matchedString)
        }
        UniversalKeyboardShortcutHandler.registerShortcut("foo", fullMatchHandler, partialMatchHandler)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("f")))
        assertEquals(listOf("f") ,matchedStringsList)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("Escape")))
        assertEquals(listOf("f", "") ,matchedStringsList)
        assertEquals(0 ,fullMatchInvoked)
    }

    @Test
    fun shouldHandleKeyPresses_resetOnUnknownKey() {
        var fullMatchInvoked = 0
        val fullMatchHandler : () -> Unit = {
            fullMatchInvoked++
        }
        val matchedStringsList = mutableListOf<String>()
        val partialMatchHandler : (String) -> Unit = { matchedString ->
            matchedStringsList.add(matchedString)
        }
        UniversalKeyboardShortcutHandler.registerShortcut("foo", fullMatchHandler, partialMatchHandler)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("f")))
        assertEquals(listOf("f") ,matchedStringsList)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("n")))
        assertEquals(listOf("f", "") ,matchedStringsList)
        assertEquals(0 ,fullMatchInvoked)
    }

    @Test
    fun shouldHandleKeyPresses_invokeFullHandlerOnMatchAndReset() {
        var fullMatchInvoked = 0
        val fullMatchHandler : () -> Unit = {
            fullMatchInvoked++
        }
        val matchedStringsList = mutableListOf<String>()
        val partialMatchHandler : (String) -> Unit = { matchedString ->
            matchedStringsList.add(matchedString)
        }
        UniversalKeyboardShortcutHandler.registerShortcut("foo", fullMatchHandler, partialMatchHandler)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("f")))
        assertEquals(listOf("f") ,matchedStringsList)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("o")))
        assertEquals(listOf("f", "fo") ,matchedStringsList)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("o")))
        assertEquals(listOf("f", "fo", "") ,matchedStringsList)
        assertEquals(1 ,fullMatchInvoked)
    }

    @Test
    fun shouldDisableHandlingOfKeyPressIfToldToDoSo() {

        var fullMatchInvoked = 0
        val fullMatchHandler : () -> Unit = {
            fullMatchInvoked++
        }
        val matchedStringsList = mutableListOf<String>()
        val partialMatchHandler : (String) -> Unit = { matchedString ->
            matchedStringsList.add(matchedString)
        }
        UniversalKeyboardShortcutHandler.registerShortcut("foo", fullMatchHandler, partialMatchHandler)
        UniversalKeyboardShortcutHandler.disableShortcuts()

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("f")))
        assertTrue(matchedStringsList.isEmpty())

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("o")))
        assertTrue(matchedStringsList.isEmpty())

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("o")))
        assertTrue(matchedStringsList.isEmpty())
        assertEquals(0 ,fullMatchInvoked)
    }

    @Test
    fun shouldInvokeEscHandlerIfRegistered() {

        var handlerInvoked = 0
        val fullMatchHandler : () -> Unit = {
            handlerInvoked++
        }
        UniversalKeyboardShortcutHandler.registerEscapeHandler(fullMatchHandler)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("Escape")))
        assertEquals(1 ,handlerInvoked)
    }

    @Test
    fun shouldHandleEnter() {
        var handlerInvoked = 0
        val fullMatchHandler : () -> Unit = {
            handlerInvoked++
        }
        UniversalKeyboardShortcutHandler.registerEnterKeyShortcut(fullMatchHandler)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("Enter")))
        assertEquals(1 ,handlerInvoked)
    }

    @Test
    fun shouldHandleNumericKeyboardShortcuts() {
        var numericCHandlerInvoked = 0
        val numericCHandler: (Int) -> Unit = { _ ->
            numericCHandlerInvoked++
        }

        var numericFHandlerInvoked = 0
        var numberForFHandler: Int?  = null
        val numericFHandler : (Int)  -> Unit = { numPressed ->
            numericFHandlerInvoked++
            numberForFHandler = numPressed
        }

        UniversalKeyboardShortcutHandler.registerNumericEndKey('c', numericCHandler)
        UniversalKeyboardShortcutHandler.registerNumericEndKey('f', numericFHandler)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("1")))
        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("2")))
        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("f")))
        assertEquals(1 , numericFHandlerInvoked)
        assertEquals(12 , numberForFHandler)
        assertEquals(0 , numericCHandlerInvoked)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("1")))
        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("2")))
        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("c")))
        assertEquals(1 , numericCHandlerInvoked)
    }

    @Test
    fun shouldInvokeEscHandlerIfNumericShortcutInvalid() {
        var numericCHandlerInvoked = 0
        val numericCHandler: () -> Unit = {
            numericCHandlerInvoked++
        }

        var escapeHandlerInvoked = 0
        val escapeHandler : () -> Unit = {
            escapeHandlerInvoked++
        }
        UniversalKeyboardShortcutHandler.registerEscapeHandler(escapeHandler)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("1")))
        assertEquals(0 , escapeHandlerInvoked)
        assertEquals(0 , numericCHandlerInvoked)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("2")))
        assertEquals(0 , escapeHandlerInvoked)
        assertEquals(0 , numericCHandlerInvoked)

        window.dispatchEvent(KeyboardEvent(type = "keydown", eventInitDict = KeyboardEventInit("c")))
        assertEquals(1 , escapeHandlerInvoked)
        assertEquals(0 , numericCHandlerInvoked)
    }

}