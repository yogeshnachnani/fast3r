package supercr.kb.components

import Avatar
import Grid
import kotlinx.css.Color
import kotlinx.css.backgroundColor
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.styledSpan
import supercr.css.TextStyles
import supercr.kb.UniversalKeyboardShortcutHandler

/**
 * Provides and displays a keyboard shortcut for a given [elementToRender]
 * The [elementToRender] is wrapped within a Grid and the [KeyboardEnabledComponent] uses up the [xsValueForShortcutChip] part to show the shortcut chip
 * The [onSelected] method is invoked when the component is selected.
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

class KeyboardEnabledComponent: RComponent<KeyboardEnabledComponentProps, KeyboardEnabledComponentState>() {

    override fun KeyboardEnabledComponentState.init() {
        selectedPortion = ""
        unselectedPortion = ""
    }


    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "space-evenly"
                alignItems = "center"
                spacing = 2
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    xs = 10
                }
                child(props.elementToRender)
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    xs = props.xsValueForShortcutChip ?: 2
                }
                /** TODO: Fix styling */
                Avatar {
                    attrs {
                        className = TextStyles.textInsertedForBalance
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

    /**
     * Register the shortcut with keyboard handler trie
     */
    override fun componentDidMount() {
        UniversalKeyboardShortcutHandler.registerShortcut(
            shortcutString = props.assignedShortcut,
            fullMatchHandler = props.onSelected,
            partialMatchHandler = this.handlePartialSelect
        )
    }

    override fun componentWillUnmount() {
        UniversalKeyboardShortcutHandler.unRegisterShortcut(props.assignedShortcut)
        props.uponUnmount(props.assignedShortcut)
    }
}

fun RBuilder.keyboardEnabledComponent(handler: KeyboardEnabledComponentProps.() -> Unit): ReactElement {
    return child(KeyboardEnabledComponent::class) {
        this.attrs(handler)
    }
}
