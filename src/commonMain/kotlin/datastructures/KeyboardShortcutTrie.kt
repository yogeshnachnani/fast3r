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
    private val keyboardShortcutTrie = KeyboardShortcutTrieNode()

    /**
     * Assuming _at max_ we'd want to 'generate' shortcuts for 26*26 items
     */
    private val MAX_COMPONENTS_SUPPORTED = 26*26
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

    fun listAvailableChars(prefixString: CharSequence): Set<Char> {
        return keyboardShortcutTrie.listAvailableChars(prefixString)
    }

    fun clear() {
        keyboardShortcutTrie.clear()
    }

    /**
     * There should almost never be a reason to actually use a [prefixString].
     * Prefer calling it with [prefixString] = null. This will ensure that we never have trie entries for "aa" as well as "aabb".
     * TODO: Take care of the corner case above
     */
    fun generatePossiblePrefixCombos(prefixString: CharSequence?, numberOfComponents: Int): List<String> {
        if (numberOfComponents > MAX_COMPONENTS_SUPPORTED) {
            throw RuntimeException("Can't generate $numberOfComponents. That's too much")
        }
        val availableCharsForPrefix = listAvailableChars(prefixString ?: "")
        return if ( numberOfComponents > availableCharsForPrefix.size ) {
            /** TODO: Figure out a better way here - right now it is doing iterating over all [availableCharsForPrefix] all the time */
            availableCharsForPrefix.fold(emptyList<String>()) { prefixesSelected, currentPrefixChar ->
                if (prefixesSelected.size == numberOfComponents) {
                    prefixesSelected
                } else {
                    val availableSuffixesForGivenChar = listAvailableChars("$currentPrefixChar")
                    val stringsWithCurrentCharPrefix = availableSuffixesForGivenChar.takeDesired(
                        numberOfComponents = minOf(availableCharsForPrefix.size, numberOfComponents - prefixesSelected.size)
                    )
                        .map {
                            "$currentPrefixChar$it"
                        }
                    prefixesSelected.plus(stringsWithCurrentCharPrefix)
                }
            }
        } else {
            availableCharsForPrefix.takeDesired(numberOfComponents)
        }
    }

    private fun Set<Char>.takeDesired(numberOfComponents: Int): List<String> {
        return this.foldIndexed(emptyList<String>()) { index , selectedChars, currentChar ->
            if (index < numberOfComponents) {
                selectedChars.plus(currentChar.toString())
            } else {
                selectedChars
            }
        }
    }
}

private class KeyboardShortcutTrieNode {
    private val children: MutableMap<Char, KeyboardShortcutTrieNode> = mutableMapOf()
    private var fullMatchHandler :( () -> Unit )? = null
    private var partialMatchHandlers: MutableList<() -> Unit> = mutableListOf()
    companion object {
        private val setOfChars = setOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
    }

    fun addShortcut(shortcutString: CharSequence, fullMatchHandler: () -> Unit, partialMatchHandler: () -> Unit) {
        when(val prefix = shortcutString.firstOrNull()) {
            null -> {
                if (this.fullMatchHandler != null) {
                    throw RuntimeException("Ye nahi ho sakta")
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

    fun listAvailableChars(prefixString: CharSequence): Set<Char> {
        return if (prefixString.isEmpty()) {
            setOfChars.minus(children.keys)
        } else {
            fetchNode(prefixString)?.availablePrefixesForNode() ?: setOfChars
        }
    }

    fun clear() {
        children.clear()
    }

    private fun addPartialMatchHandler(partialMatchHandler: () -> Unit) {
        this.partialMatchHandlers.add(partialMatchHandler)
    }

    private fun fetchNode(forString: CharSequence): KeyboardShortcutTrieNode? {
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

    private fun availablePrefixesForNode(): Set<Char> {
        return setOfChars.minus(children.keys)
    }

}

fun PrefixMatchResult.isNoMatch(): Boolean {
    return this.fullMatch == null && this.partialMatches.isEmpty()
}
