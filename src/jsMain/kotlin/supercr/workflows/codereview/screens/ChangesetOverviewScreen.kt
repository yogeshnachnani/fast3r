package supercr.workflows.codereview.screens

import Grid
import codereview.FileDiffListV2
import datastructures.KeyboardShortcutTrie
import git.provider.PullRequestSummary
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.setState
import styled.getClassName
import supercr.css.ComponentStyles
import supercr.kb.components.enterActivatedButton
import supercr.workflows.codereview.components.FileDiffAndShortcut
import supercr.workflows.codereview.components.changeSummary
import supercr.workflows.codereview.components.dndFileList
import supercr.workflows.codereview.components.titleAndDescription

external interface ChangesetOverviewScreenProps: RProps {
    var fileDiffList: FileDiffListV2
    var pullRequestSummary: PullRequestSummary
    var handleStartReview: (FileDiffListV2) -> Unit
}

external interface ChangeSetOverviewScreenState: RState {
    var fileDiffListAndShortcuts: List<FileDiffAndShortcut>
}

/**
 * Main Screen to start off reviews with.
 * Should aim to show
 * (a) Summary of the PR
 * (b) Files changed etc
 * (c) Stats
 * (d) Comments
 */
class ChangesetOverviewScreen constructor(
    constructorProps: ChangesetOverviewScreenProps
) : RComponent<ChangesetOverviewScreenProps, ChangeSetOverviewScreenState>(constructorProps) {

    override fun ChangeSetOverviewScreenState.init(props: ChangesetOverviewScreenProps) {
        val kbShortcuts = KeyboardShortcutTrie.generatePossiblePrefixCombos(
            prefixString = "d",
            numberOfComponents = props.fileDiffList.fileDiffs.size
        )
        fileDiffListAndShortcuts = props.fileDiffList.fileDiffs.mapIndexed { index, fileDiffV2 ->
            FileDiffAndShortcut(
                fileDiffV2 = fileDiffV2,
                kbShortcut = kbShortcuts[index],
                handlerForFile = noOpFileHandler
            )
        }
    }

    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                spacing = 0
                justify = "center"
                className = ComponentStyles.getClassName { ComponentStyles::fullHeight }
            }
            renderLeftSide()
            renderCenterContent()
            renderRightSideContent()
        }
    }

    private fun RBuilder.renderCenterContent() {
        Grid {
            attrs {
                container = false
                item = true
                md = 4
            }
            Grid {
                attrs {
                    container = true
                    item = false
                    alignItems = "center"
                    alignContent = "center"
                    spacing = 1
                    className = ComponentStyles.getClassName { ComponentStyles::fullHeight }
                }
                Grid {
                    attrs {
                        item = true
                        container = false
                        md = 12
                    }
                    titleAndDescription {
                        pullRequestSummary = props.pullRequestSummary
                    }
                }
                Grid {
                    attrs {
                        item = true
                        container = false
                        md = 12
                    }
                    changeSummary {
                        fileDiffList = props.fileDiffList
                    }
                }
                Grid {
                    attrs {
                        item = true
                        container = false
                        md = 12
                    }
                    Grid {
                        attrs {
                            container = true
                            item = false
                            justify = "center"
                            spacing = 4
                        }
                        Grid {
                            attrs {
                                item = true
                                container = false
                            }
                            enterActivatedButton {
                                label = "Start Review"
                                onSelected = startReviewCallback
                            }
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.renderLeftSide() {
        Grid {
            attrs {
                container = false
                item = true
                md = 4
            }
            dndFileList {
                fileList = state.fileDiffListAndShortcuts
                handleReordering = handleReorderingOfFiles
            }
        }
    }

    private val startReviewCallback : () -> Unit = {
        val reorderedFileList = state.fileDiffListAndShortcuts.map { it.fileDiffV2 }
        props.handleStartReview(FileDiffListV2(fileDiffs = reorderedFileList))
    }

    private fun RBuilder.renderRightSideContent() {
        Grid {
            attrs {
                container = false
                item = true
                md = 4
            }
        }
    }

    private val noOpFileHandler: () -> Unit = {

    }
    private val handleReorderingOfFiles: (Int, Int) -> Unit = { fromIndex, toIndex ->
        val newList = state.fileDiffListAndShortcuts.map { it.copy() }.toMutableList()
        val movedFile = newList.removeAt(fromIndex)
        newList.add(toIndex, movedFile)
        setState {
            fileDiffListAndShortcuts = newList
        }
    }

}

fun RBuilder.changeSetOverviewScreen(handler: ChangesetOverviewScreenProps.() -> Unit): ReactElement {
    return child(ChangesetOverviewScreen::class) {
        this.attrs(handler)
    }
}
