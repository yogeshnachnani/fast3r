package datastructures

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class KeyboardShortcutTrieNodeTest {

    @BeforeTest
    fun clear() {
        KeyboardShortcutTrie.clear()
    }

    @Test
    fun testAdditionAndCompleteMatch() {
        val noOp: () -> Unit = {

        }
        var abCalled = false
        val functionForAb: () -> Unit = {
            abCalled = true
        }
        var abcCalled = false
        val functionForAbc: () -> Unit = {
            abcCalled = true
        }
        var bbcCalled = false
        val functionForbbc: () -> Unit = {
            bbcCalled = true
        }

        KeyboardShortcutTrie["abc"] = PrefixMatchHandlers(fullMatch = functionForAbc, partialMatchHandler = noOp)
        KeyboardShortcutTrie["ab"] = PrefixMatchHandlers(fullMatch = functionForAb, partialMatchHandler = noOp)
        KeyboardShortcutTrie["bbc"] = PrefixMatchHandlers(fullMatch = functionForbbc, partialMatchHandler = noOp)

        KeyboardShortcutTrie["abc"].fullMatch!!.invoke()
        assertTrue(abcCalled)
        assertFalse(abCalled)
        assertFalse(bbcCalled)

        KeyboardShortcutTrie["ab"].fullMatch!!.invoke()
        assertTrue(abCalled)

        KeyboardShortcutTrie["bbc"].fullMatch!!.invoke()
        assertTrue(bbcCalled)

        assertNull(KeyboardShortcutTrie["foobarbaaz"].fullMatch)
    }

    @Test
    fun testAvailabilityOfChars() {
        val noOp: () -> Unit = {

        }
        assertEquals(24 , KeyboardShortcutTrie.listAvailableChars("").count())
        assertEquals(24 , KeyboardShortcutTrie.listAvailableChars("a").count())
        KeyboardShortcutTrie["a"] = PrefixMatchHandlers(noOp, noOp)
        assertEquals(23 , KeyboardShortcutTrie.listAvailableChars("").count())
        /**
         * The following test means that we can make 24 strings starting with the prefix 'a' : aa, ab, ac.. az
         */
        assertEquals(24 , KeyboardShortcutTrie.listAvailableChars("a").count())
        assertEquals(24 , KeyboardShortcutTrie.listAvailableChars("b").count())
        assertEquals(24 , KeyboardShortcutTrie.listAvailableChars("abc").count())

    }

    @Test
    fun testPrefixMatchResultingInFullMatch() {
        val noOp: () -> Unit = {

        }
        var component1PartialMatch = 0
        val partialMatchForComponent1: () -> Unit = {
            component1PartialMatch++
        }
        var component2PartialMatch = 0
        val partialMatchForComponent2: () -> Unit = {
            component2PartialMatch++
        }
        var component3PartialMatch = 0
        val partialMatchForComponent3: () -> Unit = {
            component3PartialMatch++
        }

        KeyboardShortcutTrie["c01"] = PrefixMatchHandlers(fullMatch = noOp, partialMatchHandler = partialMatchForComponent1)
        KeyboardShortcutTrie["c02"] = PrefixMatchHandlers(fullMatch = noOp, partialMatchHandler = partialMatchForComponent2)
        KeyboardShortcutTrie["c03"] = PrefixMatchHandlers(fullMatch = noOp, partialMatchHandler = partialMatchForComponent3)

        /** Now, we simulate key presses, to test how partial matches work */
        /** User pressed c */
        KeyboardShortcutTrie["c"]
            .also { (fullMatchHandler, partialMatchHandlers) ->
                assertNull(fullMatchHandler)
                partialMatchHandlers.forEach { it.invoke() }
            }

        /** User pressed 0 */
        KeyboardShortcutTrie["c0"]
            .also { (fullMatchHandler, partialMatchHandlers) ->
                assertNull(fullMatchHandler)
                partialMatchHandlers.forEach { it.invoke() }
            }
        /** User pressed 1. This will match c01 completely and we should get only a fullMatchHandler and no partial match handlers */
        KeyboardShortcutTrie["c01"]
            .also { (fullMatchHandler, partialMatchHandlers) ->
                assertNotNull(fullMatchHandler)
                assertTrue(partialMatchHandlers.isEmpty())
            }

        assertEquals(2, component1PartialMatch )
        assertEquals(2, component2PartialMatch)
        assertEquals(2, component3PartialMatch)
    }

    @Test
    fun testPrefixMatchResultingInPartialMatch() {
        val noOp: () -> Unit = {

        }
        var component1PartialMatch = 0
        val partialMatchForComponent1: () -> Unit = {
            component1PartialMatch++
        }
        var component2PartialMatch = 0
        val partialMatchForComponent2: () -> Unit = {
            component2PartialMatch++
        }
        var component3PartialMatch = 0
        val partialMatchForComponent3: () -> Unit = {
            component3PartialMatch++
        }

        KeyboardShortcutTrie["c01"] = PrefixMatchHandlers(fullMatch = noOp, partialMatchHandler = partialMatchForComponent1)
        KeyboardShortcutTrie["c02"] = PrefixMatchHandlers(fullMatch = noOp, partialMatchHandler = partialMatchForComponent2)
        KeyboardShortcutTrie["c03"] = PrefixMatchHandlers(fullMatch = noOp, partialMatchHandler = partialMatchForComponent3)

        /** Now, we simulate key presses, to test how partial matches work */
        /** User pressed c */
        KeyboardShortcutTrie["c"]
            .also { (fullMatchHandler, partialMatchHandlers) ->
                assertNull(fullMatchHandler)
                partialMatchHandlers.forEach { it.invoke() }
            }

        /** User pressed 1 */
        KeyboardShortcutTrie["c1"]
            .also {result ->
                assertTrue(result.isNoMatch())
           }
        assertEquals(1, component1PartialMatch )
        assertEquals(1, component2PartialMatch)
        assertEquals(1, component3PartialMatch)
    }

    @Test
    fun testPrefixGeneration_BadInputShouldBomb() {
        try {
            KeyboardShortcutTrie.generatePossiblePrefixCombos("ab", 26*26 + 1)
            fail("Should have failed")
        } catch (expectedException: RuntimeException) {
            //expected
        }
    }

    @Test
    fun testPrefixGeneration_emptyPrefixOfSize1() {
        /**
         * We want 26 components - all shortcuts should fit in a single character
         */
        KeyboardShortcutTrie.generatePossiblePrefixCombos(prefixString = null, numberOfComponents = 24)
            .also { results ->
                assertEquals(24, results.size)
                results.forEachIndexed { _, s ->
                    assertEquals(1, s.count())
                }
            }
    }

    @Test
    fun testPrefixGeneration_emptyPrefix_WithPreviousShortcutAssigned() {
        val noOp: () -> Unit = {

        }
        KeyboardShortcutTrie["a"] = PrefixMatchHandlers(noOp, noOp)
        /**
         * We want 26 components - but 1 component "a" is already reserved, so we'll get shortcuts of length 2
         */
        KeyboardShortcutTrie.generatePossiblePrefixCombos(prefixString = null, numberOfComponents = 26)
            .also { results ->
                assertEquals(26, results.size)
                results.forEachIndexed { _, s ->
                    assertEquals(2, s.count())
                }
            }
    }

    @Test
    fun testPrefixGeneration_emptyPrefixOfSize2() {
        /**
         * We desire 115 components - we'll prefer to generate all shortcuts of uniform length to accommodate them
         */
        KeyboardShortcutTrie.generatePossiblePrefixCombos(prefixString = null, numberOfComponents = 115)
            .also { results ->
                assertEquals(115, results.size)
                results.forEachIndexed { _, s ->
                    assertEquals(2, s.count())
                }
            }
    }

    @Test
    fun testPrefixGeneration_withDesiredPrefixFull() {
        val noOp: () -> Unit = {

        }
        val setOfChars = setOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
        setOfChars.forEach {
            KeyboardShortcutTrie["a${it}"] = PrefixMatchHandlers(noOp, noOp)
        }

        KeyboardShortcutTrie.generatePossiblePrefixCombos(prefixString =  "a", numberOfComponents = 1)
            .also {
                assertTrue(it.isEmpty())
            }
    }

    @Test
    fun testPrefixGeneration_withDesiredPrefixAvailable() {
        val noOp: () -> Unit = {

        }
        KeyboardShortcutTrie["ab"] = PrefixMatchHandlers(noOp, noOp)

        KeyboardShortcutTrie.generatePossiblePrefixCombos(prefixString =  "a", numberOfComponents = 23)
            .also { results ->
                assertEquals(23, results.size)
                results.forEachIndexed { _, s ->
                    assertEquals(1, s.count())
                }
            }

        KeyboardShortcutTrie.generatePossiblePrefixCombos(prefixString =  "a", numberOfComponents = 55)
            .also { results ->
                assertEquals(55, results.size)
                results.forEachIndexed { _, s ->
                    assertEquals(2, s.count())
                }
            }
    }

    @Test
    fun testRemoval() {
        val noOp: () -> Unit = {

        }
        var abCalled = false
        val functionForAb: () -> Unit = {
            abCalled = true
        }
        /** Just ensuring that we have "ab" inserted first */
        KeyboardShortcutTrie["ab"] = PrefixMatchHandlers(fullMatch = functionForAb, partialMatchHandler = noOp)
        KeyboardShortcutTrie["a"] = PrefixMatchHandlers(fullMatch = functionForAb, partialMatchHandler = noOp)
        assertEquals(23, KeyboardShortcutTrie.listAvailableChars("").size)
        assertEquals(23, KeyboardShortcutTrie.listAvailableChars("a").size)
        assertNotNull(KeyboardShortcutTrie["ab"].fullMatch)
        assertNotNull(KeyboardShortcutTrie["a"].fullMatch)

        KeyboardShortcutTrie.remove("ab")
        assertEquals(23, KeyboardShortcutTrie.listAvailableChars("").size)
        assertEquals(24, KeyboardShortcutTrie.listAvailableChars("a").size)
        assertTrue(KeyboardShortcutTrie["ab"].isNoMatch())

        KeyboardShortcutTrie.remove("a")
        assertEquals(24, KeyboardShortcutTrie.listAvailableChars("").size)
        assertTrue(KeyboardShortcutTrie["a"].isNoMatch())
    }

}