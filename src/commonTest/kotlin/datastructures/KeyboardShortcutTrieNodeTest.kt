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
    fun testTwoLetterShortcutGeneration_BadInputShouldBomb() {
        try {
            KeyboardShortcutTrie.generateTwoLetterCombos(255, 'd')
            fail("Should have failed")
        } catch (expectedException: RuntimeException) {
            //expected
        }
    }

    @Test
    fun testTwoLetterShortcutGeneration_emptyPrefix_WithPreviousShortcutAssigned() {
        val noOp: () -> Unit = {

        }
        KeyboardShortcutTrie["ah"] = PrefixMatchHandlers(noOp, noOp)
        /**
         * We want 26 components - but 1 component "a" is already reserved, so we'll get shortcuts of length 2
         */
        KeyboardShortcutTrie.generateTwoLetterCombos(numberOfComponents = 26, firstLetterPreference = 'a')
            .also { results ->
                assertEquals(26, results.size)
                results.forEachIndexed { _, s ->
                    assertEquals(2, s.count())
                }
                assertFalse(results.contains("ah"))
            }
    }

    @Test
    fun testTwoLetterShortcutGeneration_withDesiredPrefixFull() {
        val noOp: () -> Unit = {

        }
        val listOfAvailableCharsFor2ndLetter = listOf('h', 'j', 'k', 'l', 'u', 'i', 'o', 'p', 'n')
        listOfAvailableCharsFor2ndLetter.forEach {
            KeyboardShortcutTrie["a${it}"] = PrefixMatchHandlers(noOp, noOp)
        }
        /**
         * Since all shortcuts beginning with 'a' are full, we will get shortcuts with other available prefixes
         */
        KeyboardShortcutTrie.generateTwoLetterCombos(numberOfComponents = 11, firstLetterPreference = 'a')
            .also { results ->
                assertEquals(9, results.filter { it.startsWith('d') }.size)
                assertEquals(2, results.filter { it.startsWith('e') }.size)
            }
    }

    @Test
    fun testPrefixGeneration_withDesiredPrefixAvailable() {
        val noOp: () -> Unit = {

        }
        KeyboardShortcutTrie["ab"] = PrefixMatchHandlers(noOp, noOp)

        KeyboardShortcutTrie.generateTwoLetterCombos(numberOfComponents = 8, firstLetterPreference = 'a')
            .also { results ->
                assertEquals(8, results.size)
                assertEquals(8, results.filter { it.startsWith('a') }.size)
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
        assertNotNull(KeyboardShortcutTrie["ab"].fullMatch)
        assertNotNull(KeyboardShortcutTrie["a"].fullMatch)

        KeyboardShortcutTrie.remove("ab")
        assertTrue(KeyboardShortcutTrie["ab"].isNoMatch())

        KeyboardShortcutTrie.remove("a")
        assertTrue(KeyboardShortcutTrie["a"].isNoMatch())
    }

    @Test
    fun testToGuardAgainstAccidentalRepeatsInSetsOfChars() {
//        assertTrue(KeyboardShortcutTrie.listOfAvailableCharsFor2ndLetter.intersect(KeyboardShortcutTrie.listOfAvailableCharsForFirstLetter).isEmpty())
    }

}