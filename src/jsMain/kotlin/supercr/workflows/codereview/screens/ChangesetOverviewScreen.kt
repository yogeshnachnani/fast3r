package supercr.workflows.codereview.screens

import AccessTime
import Battery20
import Grid
import codereview.FileDiffListV2
import codereview.Project
import datastructures.KeyboardShortcutTrie
import git.provider.PullRequestSummary
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.display
import kotlinx.css.flexGrow
import kotlinx.css.justifyContent
import kotlinx.css.marginBottom
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width
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
import supercr.kb.components.enterActivatedButton
import supercr.kb.components.iconAndLogoutButton
import supercr.kb.components.pullRequestAgeRibbon
import supercr.workflows.codereview.components.FileDiffAndShortcut
import supercr.workflows.codereview.components.dndFileList
import supercr.workflows.codereview.components.reviewCommentsList

external interface ChangesetOverviewScreenProps: RProps {
    var fileDiffList: FileDiffListV2
    var pullRequestSummary: PullRequestSummary
    var handleStartReview: (FileDiffListV2) -> Unit
    var project: Project
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
        val kbShortcuts = KeyboardShortcutTrie.generateTwoLetterCombos(numberOfComponents = props.fileDiffList.fileDiffs.size)
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
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 12
                }
                iconAndLogoutButton {}
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 5
                }
                renderCenterContentV2()
            }
        }
    }

    private fun RBuilder.renderCenterContentV2() {
        styledDiv {
            css {
                width = 100.pct
            }
            renderTitleAndAge()
            renderPRMetaInfo()
            reviewCommentsList {

            }
            startReviewButton()
        }
    }

    private fun RBuilder.startReviewButton() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "flex-end"
                alignItems = "center"
                spacing = 2
            }
            Grid {
                attrs {
                    item = true
                    container = false
                }
                styledSpan {
                    css {
                        + ComponentStyles.changeSetOverviewPressEnterText
                    }
                    + "Press Enter ↵"
                }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                    md = 1
                }
                enterActivatedButton {
                    label = "Review"
                    onSelected = startReviewCallback
                    buttonClazz = ComponentStyles.getClassName { ComponentStyles::changeSetReviewButton }
                }
            }
        }
    }

    private fun RBuilder.renderPRMetaInfo() {
        styledDiv {
            css {
                + ComponentStyles.changeSetOverviewMetaInfo
            }
            styledDiv {
                css {
                    flexGrow = 1.0
                    display = Display.flex
                    justifyContent = JustifyContent.center
                }
                styledDiv {
                    css {
                        + ComponentStyles.changeSetOverviewMetaInfoProjectName
                    }
                    + props.project.name
                }
            }
            /** Estimated Time */
            styledDiv {
                css {
                    + ComponentStyles.changeSetOverviewMetaInfoEstTime
                }
                AccessTime {
                    attrs {
                        fontSize = "inherit"
                    }
                }
                styledSpan {
                    css {
                        + ComponentStyles.pullRequestSummaryMetaDataText
                    }
                    + " Est. Time: 10mins "
                }
            }
            /** Size */
            styledDiv {
                css {
                    css {
                        + ComponentStyles.pullRequestSummaryMetaDataSize
                    }
                }
                Battery20 {
                    attrs {
                        fontSize = "inherit"
                    }
                }
                styledSpan {
                    css {
                        + ComponentStyles.pullRequestSummaryMetaDataText
                    }
                    + " Size: XS "
                }
            }
        }
    }

    private fun RBuilder.renderTitleAndAge() {
        styledDiv {
            css {
                + ComponentStyles.changeSetOverviewTitleAndAge
            }
            styledP {
                css {
                    width = 80.pct
                    marginBottom = 0.px
                }
                + props.pullRequestSummary.title
            }
            pullRequestAgeRibbon {
                pullRequestSummary = props.pullRequestSummary
                roundedBothSides = true
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
