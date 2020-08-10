package supercr.workflows.codereview.components

import ListItem
import MaterialUIList
import kotlinx.css.basis
import kotlinx.css.flexBasis
import kotlinx.css.pct
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.getClassName
import styled.styledDiv
import supercr.css.ComponentStyles
import supercr.kb.components.keyboardChip

external interface NumberBasedReOrderableListProps : RProps {
    var children: List<() -> ReactElement>
    var itemClazz : String
    var handleReorderingFn: (Int, Int) -> Unit
    var listClazz: String
}

external interface NumberBasedReOrderableListState : RState {

}

class NumberBasedReOrderableList : RComponent<NumberBasedReOrderableListProps, NumberBasedReOrderableListState>() {
    override fun RBuilder.render() {
        MaterialUIList {
            attrs {
                className = props.listClazz
            }
            props.children.mapIndexed { index, childRenderfn ->
                ListItem {
                    attrs {
                        className = props.itemClazz
                    }
                    styledDiv {
                        css {
                            flexBasis = 85.pct.basis
                        }
                        child(childRenderfn.invoke())
                    }
                    styledDiv {
                        css {
                            flexBasis = 15.pct.basis
                        }
                        keyboardChip {
                            attrs {
                                onSelected = {

                                }
                                assignedShortcut = if (props.children.size > 9 && index <= 9) {
                                    "0$index"
                                } else {
                                    index.toString()
                                }
                                uponUnmount = handleShortcutUnmount
                                className = ComponentStyles.getClassName { ComponentStyles::pullRequestSummaryCardKeyboardShortcut }
                            }
                        }
                    }
                }
            }
        }
    }
    private val handleShortcutUnmount: (String) -> Unit = { _ ->
        //no op
    }
}

fun RBuilder.numberBasedReOrderableList(handler: NumberBasedReOrderableListProps.() -> Unit): ReactElement {
    return child(NumberBasedReOrderableList::class) {
        this.attrs(handler)
    }
}