package supercr.kb.components

import ListItem
import MaterialUIList
import datastructures.KeyboardShortcutTrie
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState

/**
 * Assigns keyboard shortcuts for given [elementsWithHandlers] and renders them in a [MaterialUIList]
 * Prefer using this method instead of directly using [KeyboardEnabledComponent]
 *
 * The given handler in [elementsWithHandlers] is invoked either when
 * (a) the element is 'clicked'
 * (b) the element is selected using a keyboard shortcut
 *
 * [listSubHeader] can be passed to add a header for the list
 */
external interface KeyboardEnabledListProps: RProps {
    var elementsWithHandlers: List<Pair<ReactElement, () -> Unit>>
    var xsValueForShortcutChip: Number?
    var listSubHeader: ReactElement?
}

external interface KeyboardEnabledListState: RState {
    var prefixes: List<Triple<String, ReactElement, () -> Unit>>
    var isInitialized: Boolean
}

class KeyboardEnabledList: RComponent<KeyboardEnabledListProps, KeyboardEnabledListState>() {
    /**
     * TODO: Figure out if there is a way to initialise state using the [props] here.
     * I tried doing the same thing that [componentDidMount] does in this method here - but that bombed (props apparently weren't present)
     */
    override fun KeyboardEnabledListState.init(props: KeyboardEnabledListProps) {
        prefixes = emptyList()
        isInitialized = false
    }

    override fun RBuilder.render() {
        if(state.isInitialized) {
            MaterialUIList {
                if (props.listSubHeader != null) {
                    child(props.listSubHeader!!)
                }
                state.prefixes.mapIndexed {index,  (prefix, element, handler) ->
                    ListItem {
                        attrs {
                            divider = true
                            onClick = handler
                        }
                        keyboardEnabledComponent {
                            elementToRender = element
                            onSelected = handler
                            assignedShortcut = prefix
                            xsValueForShortcutChip = props.xsValueForShortcutChip
                            uponUnmount = removePrefixOnUnmount
                        }
                    }
                }
            }
        }
    }

    override fun componentDidMount() {
        val generatedPrefixes = KeyboardShortcutTrie.generatePossiblePrefixCombos(null, props.elementsWithHandlers.size).toMutableList()
        val newList = props.elementsWithHandlers.mapIndexed { index, (element, handler) ->
            Triple(generatedPrefixes[index], element, handler)
        }
        setState {
            prefixes = newList
            isInitialized = true
        }
    }

    private val removePrefixOnUnmount : (String) -> Unit = { prefixToRemove ->
        val isPrefixPresent = state.prefixes.any { it.first == prefixToRemove }
        if (isPrefixPresent) {
            val newList = state.prefixes.filterNot {
                it.first == prefixToRemove
            }
            setState {
                prefixes = newList
            }
        }
    }
}

fun RBuilder.keyboardEnabledList(handler: KeyboardEnabledListProps.() -> Unit): ReactElement {
    return child(KeyboardEnabledList::class) {
        this.attrs(handler)
    }
}
