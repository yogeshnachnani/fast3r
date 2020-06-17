package supercr.kb.components

import Avatar
import Grid
import kotlinx.css.Color
import kotlinx.css.backgroundColor
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.getClassName
import styled.styledSpan
import supercr.css.ComponentStyles
import supercr.kb.UniversalKeyboardShortcutHandler

/**
 * Takes care of registering and unregistering the assigned shortcuts with the [UniversalKeyboardShortcutHandler]
 */
external interface KeyboardChipProps: RProps {
    var onSelected: () -> Unit
    var assignedShortcut: String
    var uponUnmount: (String) -> Unit
}

external interface KeyboardChipState: RState {
    var selectedPortion: String
    var unselectedPortion: String
}

class KeyboardChip(
    constructorProps: KeyboardChipProps
): RComponent<KeyboardChipProps, KeyboardChipState>(constructorProps) {

    /**
     * Register the shortcut with keyboard handler trie
     */
    override fun KeyboardChipState.init(props: KeyboardChipProps) {
        selectedPortion = ""
        unselectedPortion = ""
    }


    override fun RBuilder.render() {
        /** TODO: Fix styling */
        Avatar {
            attrs {
                variant = "rounded"
                className = "${ ComponentStyles.getClassName { ComponentStyles::smallTextAvatar } } ${ComponentStyles.getClassName { ComponentStyles::backgroundAccentPrimary4 }}"
            }
            if (state.selectedPortion.isNotEmpty()) {
                styledSpan {
                    css.backgroundColor = Color.lightGreen
                    + state.selectedPortion
                }
                + state.unselectedPortion
            } else {
                + props.assignedShortcut
            }
        }
    }

    private val handlePartialSelect: (String) -> Unit = { selectedString->
        setState {
            selectedPortion = selectedString
            unselectedPortion = props.assignedShortcut.removePrefix(selectedString)
        }
    }

    override fun componentDidMount() {
        UniversalKeyboardShortcutHandler.registerShortcut(
            shortcutString = props.assignedShortcut,
            fullMatchHandler = props.onSelected,
            partialMatchHandler = this@KeyboardChip.handlePartialSelect
        )
    }

    override fun componentWillUnmount() {
        UniversalKeyboardShortcutHandler.unRegisterShortcut(props.assignedShortcut)
        props.uponUnmount(props.assignedShortcut)
    }
}

fun RBuilder.keyboardChip(handler: RElementBuilder<KeyboardChipProps>.() -> Unit): ReactElement {
    return child(KeyboardChip::class) {
        handler()
    }
}
