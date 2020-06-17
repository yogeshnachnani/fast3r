package supercr.kb.components

import Avatar
import Grid
import datastructures.KeyboardShortcutTrie
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
import supercr.css.TextStyles
import supercr.kb.UniversalKeyboardShortcutHandler

/**
 * Provides and displays a keyboard shortcut for a given [elementToRender]
 * The [elementToRender] is wrapped within a Grid and the [KeyboardEnabledComponent] uses up the [xsValueForShortcutChip] part to show the shortcut chip
 * The [onSelected] method is invoked when the component is selected.
 * The only advantage provided by this is that it takes care of registering and unregistering the assigned shortcuts with the [UniversalKeyboardShortcutHandler]
 */
external interface KeyboardEnabledComponentProps: RProps {
    var elementToRender: ReactElement
    var xsValueForShortcutChip: Number?
    var onSelected: () -> Unit
    var assignedShortcut: String
    var uponUnmount: (String) -> Unit
}

external interface KeyboardEnabledComponentState: RState {
    var selectedPortion: String
    var unselectedPortion: String
}

class KeyboardEnabledComponent(
    constructorProps: KeyboardEnabledComponentProps
): RComponent<KeyboardEnabledComponentProps, KeyboardEnabledComponentState>(constructorProps) {

    /**
     * Register the shortcut with keyboard handler trie
     */
    override fun KeyboardEnabledComponentState.init(props: KeyboardEnabledComponentProps) {
        selectedPortion = ""
        unselectedPortion = ""
    }


    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "space-around"
                alignItems = "baseline"
                spacing = 1
            }
            Grid {
                attrs {
                    container = false
                    item = true
                }
                child(props.elementToRender)
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = props.xsValueForShortcutChip ?: 2
                }
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
            partialMatchHandler = this@KeyboardEnabledComponent.handlePartialSelect
        )
    }

    override fun componentWillUnmount() {
        UniversalKeyboardShortcutHandler.unRegisterShortcut(props.assignedShortcut)
        props.uponUnmount(props.assignedShortcut)
    }
}

fun RBuilder.keyboardEnabledComponent(handler: RElementBuilder<KeyboardEnabledComponentProps>.() -> Unit): ReactElement {
    return child(KeyboardEnabledComponent::class) {
        handler()
    }
}
