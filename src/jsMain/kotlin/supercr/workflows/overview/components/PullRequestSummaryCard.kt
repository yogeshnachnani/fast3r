package supercr.workflows.overview.components

import AccessTime
import Avatar
import Battery20
import codereview.Project
import git.provider.PullRequestSummary
import kotlinx.css.Align
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.FontWeight
import kotlinx.css.JustifyContent
import kotlinx.css.LinearDimension
import kotlinx.css.alignItems
import kotlinx.css.backgroundColor
import kotlinx.css.basis
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.flexBasis
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.justifyContent
import kotlinx.css.minHeight
import kotlinx.css.pct
import kotlinx.css.width
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.css
import styled.getClassName
import styled.styledDiv
import styled.styledP
import styled.styledSpan
import supercr.css.Colors
import supercr.css.ComponentStyles
import supercr.css.EditorThemeColors
import supercr.css.FontSizes
import supercr.kb.components.keyboardChip
import supercr.kb.components.projectNameChip
import supercr.kb.components.pullRequestAgeRibbon
import supercr.utils.ageInHoursFromNow
import supercr.utils.getAgeFromNow
import supercr.utils.pickAgeRibbonColor

external interface PullRequestSummaryCardProps : RProps {
    var project: Project
    var pullRequestSummary: PullRequestSummary
    var onClickHandler : () -> Unit
    var assignedKeyboardShortcut: String
}

external interface PullRequestSummaryCardState : RState {

}

class PullRequestSummaryCard : RComponent<PullRequestSummaryCardProps, PullRequestSummaryCardState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                + ComponentStyles.pullRequestSummaryCard
            }
            summaryHeader()
            projectName()
            summaryComment()
            summaryMetaData()
        }
    }

    private fun RBuilder.summaryMetaData() {
        styledDiv {
            css {
                + ComponentStyles.pullRequestMetaDataContainer
            }
            styledDiv {
                css {
                    + ComponentStyles.pullRequestMetaDataItems
                }
                /** Estimated Time */
                styledDiv {
                    css {
                        + ComponentStyles.pullRequestSummaryMetaDataEstTime
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
            keyboardChip {
                attrs {
                    onSelected = props.onClickHandler
                    assignedShortcut = props.assignedKeyboardShortcut
                    uponUnmount = handleShortcutUnmount
                    className = ComponentStyles.getClassName { ComponentStyles::pullRequestSummaryCardKeyboardShortcut }
                }
            }
        }
    }

    private fun RBuilder.summaryComment() {
        styledDiv {
            css {
                + ComponentStyles.pullRequestSummaryCommentContainer
            }
            styledDiv {
                css {
                    + ComponentStyles.pullRequestSummaryCommentUserAvatar
                }
                Avatar {
                    attrs {
                        className = ComponentStyles.getClassName { ComponentStyles::avatarInitials }
                    }
                    + "YN"
                }
            }
            styledDiv {
                css {
                    width = 80.pct
                }
                styledP {
                    css {
                        fontWeight = FontWeight.w600
                    }
                    + props.pullRequestSummary.user.login
                }
                styledDiv {
                    css {
                        + ComponentStyles.pullRequestSummaryCommentBody
                    }
                    + props.pullRequestSummary.body
                }
            }
        }
    }

    private fun RBuilder.projectName() {
        styledDiv {
            css {
                + ComponentStyles.pullRequestSummaryProjectName
            }
            + props.project.name
        }
    }

    private fun RBuilder.summaryHeader() {
        styledDiv {
            css {
                + ComponentStyles.pullRequestSummaryHeaderContainer
            }
            styledP {
                css {
                    + ComponentStyles.pullRequestSummaryCardHeading
                }
                + props.pullRequestSummary.title
            }
            pullRequestAgeRibbon {
                pullRequestSummary = props.pullRequestSummary
                roundedBothSides = false
            }
        }
    }

    private val handleShortcutUnmount: (String) -> Unit = { _ ->
        //no op
    }

}

fun RBuilder.pullRequestSummaryCard(handler: PullRequestSummaryCardProps.() -> Unit): ReactElement {
    return child(PullRequestSummaryCard::class) {
        this.attrs(handler)
    }
}