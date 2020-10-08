package supercr.kb.components

import ListItem
import MaterialUIList
import Paper
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.css.marginRight
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.ComponentStyles
import supercr.kb.UniversalKeyboardShortcutHandler
import supercr.kb.UniversalShortcuts
import supercr.kb.noOpPartialMatchHandler

external interface HelpBoxProps : RProps {
    /**
     * To keep things simple, the box props consist of a list of <String, List<ReactElement>> pairs.
     * Where <String> is rendered as a "Heading" and the corresponding List<> is rendered inside of a MaterialUIList.
     * The fact that the comment talks about implementation detail itself should tell you how much of a hurry this was written in
     */
    var contents: List<Pair<String, List<ReactElement>>>

}

external interface HelpBoxState : RState {
    var showHelpBox: Boolean
}

/**
 * A Generic Help box.
 * This will reveal itself whenever the user presses '?'
 *
 */
class HelpBox : RComponent<HelpBoxProps, HelpBoxState>() {

    override fun HelpBoxState.init() {
        showHelpBox = false
    }

    override fun RBuilder.render() {
        if (state.showHelpBox) {
            Paper {
                attrs {
                    className = ComponentStyles.getClassName { ComponentStyles::helpBoxContainer }
                    elevation = 6
                    variant = "elevation"
                }
                styledDiv {
                    /** Main header for the Help box */
                    css {
                        + ComponentStyles.helpBoxHeader
                    }
                    styledSpan {
                        css {
                            display = Display.inlineFlex
                        }
                        + "Fast3r Help"
                    }
                    styledDiv {
                        styledSpan {
                            css {
                                +ComponentStyles.helpBoxHeaderCloseActionHelp
                            }
                            +"Press Esc To"
                        }
                        + "Close"
                    }
                }
                props.contents.map { (headingText, listOfHelpItems) ->
                    renderHeading(headingText)
                    renderHelpItems(listOfHelpItems)
                }
            }
        }
    }

    private fun RBuilder.renderHelpItems(listOfHelpItems: List<ReactElement>) {
        MaterialUIList {
            listOfHelpItems.map { reactElement ->
                ListItem {
                    child(reactElement)
                }
            }
        }
    }

    private fun RBuilder.renderHeading(headingText: String): ReactElement {
        return styledP {
            css {
                +ComponentStyles.helpBoxSectionHeading
            }
            + headingText
        }
    }

    override fun componentWillUpdate(nextProps: HelpBoxProps, nextState: HelpBoxState) {
        if(nextState.showHelpBox) {
            /** The component will be rendered. Register the escape handler */
            UniversalKeyboardShortcutHandler.registerEscapeHandler(escapeHandler)
        } else {
            /** The component will be hidden */
            UniversalKeyboardShortcutHandler.unregisterEscapeKeyShortcut()
        }
    }

    override fun componentDidMount() {
        UniversalKeyboardShortcutHandler.registerShortcut(UniversalShortcuts.Help.shortcutString, toggleHelpBox, noOpPartialMatchHandler)
    }

    override fun componentWillUnmount() {
        UniversalKeyboardShortcutHandler.unRegisterShortcut(UniversalShortcuts.Help.shortcutString)
        UniversalKeyboardShortcutHandler.unregisterEscapeKeyShortcut()
    }



    private val toggleHelpBox: () -> Unit = {
        val newValue = state.showHelpBox.not()
        setState {
            showHelpBox = newValue
        }
    }

    private val escapeHandler: () -> Unit = {
        setState {
            showHelpBox = false
        }
    }
}

fun RBuilder.helpBox(handler: HelpBoxProps.() -> Unit): ReactElement {
    return child(HelpBox::class) {
        this.attrs(handler)
    }
}

external interface KeyboardShortcutExplainerProps : RProps {
    var keyboardShortcutString: String
    var helpText: String
}

external interface KeyboardShortcutExplainerState : RState {

}

/**
 * Renders the shortcut keys and the help text associated with it
 */
class KeyboardShortcutExplainer : RComponent<KeyboardShortcutExplainerProps, KeyboardShortcutExplainerState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
            }
            props.keyboardShortcutString.map { shortcutChar ->
                styledDiv {
                    css {
                        + ComponentStyles.keyboardShortcutSingleCharBox
                        marginRight = 12.px
                    }
                    + "$shortcutChar"
                }
            }
            styledDiv {
                css {
                    + ComponentStyles.helpBoxText
                }
                styledSpan {
                    + props.helpText
                }
            }
        }
    }
}

fun RBuilder.keyboardShortcutExplainer(handler: KeyboardShortcutExplainerProps.() -> Unit): ReactElement {
    return child(KeyboardShortcutExplainer::class) {
        this.attrs(handler)
    }
}
