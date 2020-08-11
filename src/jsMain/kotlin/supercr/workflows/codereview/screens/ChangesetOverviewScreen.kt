package supercr.workflows.codereview.screens

import AccessTime
import Battery20
import Grid
import codereview.FileDiffListV2
import codereview.FileDiffV2
import codereview.Project
import git.provider.PullRequestSummary
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.LinearDimension
import kotlinx.css.basis
import kotlinx.css.display
import kotlinx.css.flex
import kotlinx.css.flexBasis
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
import react.buildElement
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
import supercr.workflows.codereview.components.numberBasedReOrderableList
import supercr.workflows.codereview.components.reviewCommentsList

external interface ChangesetOverviewScreenProps: RProps {
    var fileDiffList: FileDiffListV2
    var pullRequestSummary: PullRequestSummary
    var handleStartReview: (FileDiffListV2) -> Unit
    var project: Project
}

external interface ChangeSetOverviewScreenState: RState {
    var reOrderAbleFileDiffList: List<FileDiffV2>
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
        reOrderAbleFileDiffList = props.fileDiffList.fileDiffs.map { it }
    }

    override fun RBuilder.render() {
        Grid {
            attrs {
                container = true
                item = false
                justify = "center"
//                spacing = 10
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
                    md = 2
                }
                renderOrderableFileList()
            }
            Grid {
                attrs {
                    container = false
                    item = true
                    md = 1
                }
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

    private fun RBuilder.renderOrderableFileList() {
        styledDiv {
            css {
                + ComponentStyles.changeSetOverviewFileListTitle
            }
            styledP {
                css {
                    width = 100.pct
                    flexBasis = 100.pct.basis
                    marginBottom = 0.px
                }
                + "Set the File Review Sequence"
            }
            styledP {
                css {
                    + ComponentStyles.changeSetOverViewFileListSubText
                }
                + "Press a number assigned to each file to reorder"
            }
        }
        numberBasedReOrderableList {
            children = state.reOrderAbleFileDiffList.map { it.renderFileInList() }
            handleReorderingFn = handleReorderingOfFiles
            itemClazz = ComponentStyles.getClassName { ComponentStyles::changeSetOverviewFileItemContainer }
            listClazz = "${ComponentStyles.getClassName { ComponentStyles::compactList }} ${ComponentStyles.getClassName { ComponentStyles::changeSetOverviewFileList }}"
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

    private fun FileDiffV2.renderFileInList(): () -> ReactElement {
        return {
            buildElement {
                styledDiv {
                    css {
                        justifyContent = JustifyContent.spaceAround
                        display = Display.inlineFlex
                        width = LinearDimension.fillAvailable
                    }
                    styledDiv {
                        css {
                            display = Display.inlineFlex
                            flex(flexBasis = 10.pct.basis)
                            justifyContent = JustifyContent.flexStart
                        }
                        + this@renderFileInList.tShirtSize.name
                    }
                    styledDiv {
                        css {
                            display = Display.inlineFlex
                            flex(flexBasis = 80.pct.basis)
                        }
                        +(this@renderFileInList.newFile?.path ?: (this@renderFileInList.oldFile!!.path)).split("/").last()
                    }
                }
            }
        }
    }

    private val startReviewCallback : () -> Unit = {
        val reorderedFileList = state.reOrderAbleFileDiffList.map { it }
        props.handleStartReview(FileDiffListV2(fileDiffs = reorderedFileList))
    }

    private val handleReorderingOfFiles: (Int, Int) -> Unit = { fromIndex, toIndex ->
        val newList = state.reOrderAbleFileDiffList.map { it.copy() }.toMutableList()
        val movedFile = newList.removeAt(fromIndex)
        newList.add(toIndex, movedFile)
        setState {
            reOrderAbleFileDiffList = newList
        }
    }

}

fun RBuilder.changeSetOverviewScreen(handler: ChangesetOverviewScreenProps.() -> Unit): ReactElement {
    return child(ChangesetOverviewScreen::class) {
        this.attrs(handler)
    }
}
