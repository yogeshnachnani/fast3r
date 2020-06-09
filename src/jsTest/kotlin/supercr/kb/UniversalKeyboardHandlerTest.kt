import datastructures.KeyboardShortcutTrie
import datastructures.isNoMatch
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.KeyboardEventInit
import supercr.kb.UniversalKeyboardShortcutHandler
import kotlin.browser.window
import kotlin.test.BeforeTest
import kotlin.test.Ignore
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
    @Ignore
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

}