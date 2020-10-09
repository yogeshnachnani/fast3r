package datastructures

data class PrefixMatchResult(
    val fullMatch: ( () -> Unit )?,
    val partialMatches: List<() -> Unit>
)

data class PrefixMatchHandlers(
    val fullMatch: (() -> Unit),
    val partialMatchHandler: () -> Unit
)

object KeyboardShortcutTrie {
    val listOfAvailableCharsForFirstLetter = listOf('a', 'd', 'e',  'g', 'q', 'r', 't', 'v', 'x', 'z')
    val listOfAvailableCharsFor2ndLetter = listOf('h', 'j', 'k', 'l', 'u', 'i', 'o', 'p', 'n')
    val MAX_COMPONENTS_SUPPORTED = listOfAvailableCharsForFirstLetter.size * listOfAvailableCharsFor2ndLetter.size
    private val keyboardShortcutTrie = KeyboardShortcutTrieNode()

    operator fun get(shortcutString: CharSequence): PrefixMatchResult {
        return keyboardShortcutTrie.fetchHandler(shortcutString)
    }

    operator fun set(shortcutString: CharSequence, prefixMatchHandlers: PrefixMatchHandlers) {
        keyboardShortcutTrie.addShortcut(
            shortcutString = shortcutString,
            fullMatchHandler = prefixMatchHandlers.fullMatch,
            partialMatchHandler = prefixMatchHandlers.partialMatchHandler
        )
    }

    fun clear() {
        keyboardShortcutTrie.clear()
    }

    /**
     * The [firstLetterPreference] is just what the name suggests - a preference. In case we can't generate shortcuts with the given prefix, we still
     * go ahead and see if we can generate it using other available prefixes
     */
    fun generateTwoLetterCombos(numberOfComponents: Int, firstLetterPreference: Char? = 'd'): List<String> {
        if (numberOfComponents > MAX_COMPONENTS_SUPPORTED) {
            throw RuntimeException("Can't generate $numberOfComponents. That's too much")
        }
        val possibleFirstCharacters = if (firstLetterPreference != null) {
            listOf(firstLetterPreference).plus(listOfAvailableCharsForFirstLetter.minus(firstLetterPreference))
        } else {
            listOfAvailableCharsForFirstLetter
        }
        return possibleFirstCharacters.fold(emptyList<String>()) { prefixesSelected, currentPrefixChar ->
            if (prefixesSelected.size == numberOfComponents) {
                prefixesSelected
            } else {
                val possible2ndCharacters =
                    keyboardShortcutTrie.fetchNode("$currentPrefixChar")?.fetchRemainingChildren(listOfAvailableCharsFor2ndLetter) ?: listOfAvailableCharsFor2ndLetter
                val stringsWithCurrentCharPrefix = possible2ndCharacters.takeDesired(
                    numberOfComponents = minOf(possibleFirstCharacters.size, numberOfComponents - prefixesSelected.size)
                )
                    .map {
                        "$currentPrefixChar$it"
                    }
                prefixesSelected.plus(stringsWithCurrentCharPrefix)
            }
        }
    }

    private fun List<Char>.takeDesired(numberOfComponents: Int): List<String> {
        return this.foldIndexed(emptyList<String>()) { index , selectedChars, currentChar ->
            if (index < numberOfComponents) {
                selectedChars.plus(currentChar.toString())
            } else {
                selectedChars
            }
        }
    }

    fun remove(shortcutString: String) {
        keyboardShortcutTrie.remove(shortcutString)
    }
}

private class KeyboardShortcutTrieNode {
    private val children: MutableMap<Char, KeyboardShortcutTrieNode> = mutableMapOf()
    private var fullMatchHandler :( () -> Unit )? = null
    private var partialMatchHandlers: MutableList<() -> Unit> = mutableListOf()


    fun addShortcut(shortcutString: CharSequence, fullMatchHandler: () -> Unit, partialMatchHandler: () -> Unit) {
        when(val prefix = shortcutString.firstOrNull()) {
            null -> {
                if (this.fullMatchHandler != null) {
                    throw RuntimeException("Cannot add shortcut since it has already been added")
                } else {
                    this.fullMatchHandler = fullMatchHandler
                }
            }
            else -> {
                if(!children.containsKey(prefix)) {
                    children[prefix] = KeyboardShortcutTrieNode()
                }
                /** This node forms a prefix for another node - so add a partialMatchHandler at this node */
                children[prefix]!!.addPartialMatchHandler(partialMatchHandler)
                /** Proceed with adding the suffix string to the trie after removing the first char */
                children[prefix]?.addShortcut(shortcutString.subSequence(1, shortcutString.length), fullMatchHandler, partialMatchHandler)
            }
        }
    }

    fun fetchHandler(shortcutString: CharSequence): PrefixMatchResult {
        return when(val prefix = shortcutString.firstOrNull()) {
            null -> {
                PrefixMatchResult(fullMatch = this.fullMatchHandler, partialMatches = this.partialMatchHandlers)
            }
            else -> {
                val childResult  = children[prefix]?.fetchHandler(shortcutString.subSequence(1, shortcutString.length))
                if (childResult != null) {
                    /**
                     * If a full match is found, that means we have a node in the tree with the exact given [shortcutString].
                     * Ensure that we don't send any partial matches in this case
                     */
                    if (childResult.fullMatch != null) {
                        childResult.copy(partialMatches = emptyList())
                    } else {
                        childResult
                    }
                } else {
                    /**
                     * We didn't find any results/children - but partial handlers have matched to the node so far. Return that
                     */
                    PrefixMatchResult(
                        fullMatch = null,
                        partialMatches = emptyList()
                    )
                }
            }
        }
    }

    fun clear() {
        children.clear()
    }

    fun remove(shortcutString: CharSequence) {
        /** First, guard against weird inputs */
        require(shortcutString.isNotEmpty()) {"Can;t remove an empty string"}
        val prefixChar = shortcutString.first()
        if (children[prefixChar]?.removeMe(shortcutString.subSequence(1, shortcutString.length)) == true) {
            children.remove(prefixChar)
        }
    }

    private fun removeMe(shortcutString: CharSequence): Boolean {
        return when(val prefixChar = shortcutString.firstOrNull()) {
            null -> {
                /** This node has matched. It should be removed */
                true
            }
            else -> {
                if (children[prefixChar]?.removeMe(shortcutString.subSequence(1, shortcutString.length)) == true) {
                    children.remove(prefixChar)
                }
                return children.isEmpty() && fullMatchHandler == null
            }
        }
    }

    private fun addPartialMatchHandler(partialMatchHandler: () -> Unit) {
        this.partialMatchHandlers.add(partialMatchHandler)
    }

    fun fetchNode(forString: CharSequence): KeyboardShortcutTrieNode? {
        return if (forString.isNullOrEmpty()) {
            null
        } else {
            when (forString.length) {
                1 -> {
                    children[forString.first()]
                }
                else -> {
                    children[forString.first()]?.fetchNode(forString.subSequence(1, forString.length))
                }
            }
        }
    }

    fun fetchRemainingChildren(superSet: List<Char>): List<Char> {
        return superSet.minus(children.keys)
    }

}

fun PrefixMatchResult.isNoMatch(): Boolean {
    return this.fullMatch == null && this.partialMatches.isEmpty()
}
