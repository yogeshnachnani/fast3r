package supercr.kb.components

import Avatar
import Grid
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.TextAlign
import kotlinx.css.backgroundColor
import kotlinx.css.display
import kotlinx.css.height
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.px
import kotlinx.css.textAlign
import kotlinx.css.width
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledSpan
import supercr.css.Colors
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
        styledDiv {
            css {
                display = Display.flex
            }
            styledDiv {
                css {
                    classes.add(ComponentStyles.getClassName { ComponentStyles::keyboardShortcutSingleCharBox })
                    marginRight = 12.px
                    if (state.selectedPortion.isNotEmpty()) {
                        backgroundColor = Colors.primaryBlue
                    }
                }
                + "${props.assignedShortcut[0]}"
            }
            styledDiv {
                css {
                    classes.add(ComponentStyles.getClassName { ComponentStyles::keyboardShortcutSingleCharBox })
                }
                    +"${props.assignedShortcut[1]}"
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
