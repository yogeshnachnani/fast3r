package supercr.kb.components

import ListItem
import MaterialUIList
import datastructures.KeyboardShortcutTrie
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement

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
}

class KeyboardEnabledList: RComponent<KeyboardEnabledListProps, KeyboardEnabledListState>() {

    override fun RBuilder.render() {
        val prefixes = KeyboardShortcutTrie.generatePossiblePrefixCombos(null, props.elementsWithHandlers.size)
        MaterialUIList {
            if (props.listSubHeader != null) {
                child(props.listSubHeader!!)
            }
            props.elementsWithHandlers.mapIndexed {index,  (element, handler) ->
                ListItem {
                    attrs {
                        divider = true
                        onClick = handler
                    }
                    keyboardEnabledComponent {
                        elementToRender = element
                        onSelected = handler
                        assignedShortcut = prefixes[index]
                        xsValueForShortcutChip = props.xsValueForShortcutChip
                    }
                }
            }
        }
    }

}

fun RBuilder.keyboardEnabledList(handler: KeyboardEnabledListProps.() -> Unit): ReactElement {
    return child(KeyboardEnabledList::class) {
        this.attrs(handler)
    }
}
