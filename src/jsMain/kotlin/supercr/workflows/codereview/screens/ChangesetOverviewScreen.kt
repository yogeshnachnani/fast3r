package supercr.workflows.codereview.screens

import Grid
import codereview.FileDiffList
import git.provider.PullRequestSummary
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.getClassName
import supercr.css.ComponentStyles
import supercr.kb.components.enterActivatedButton
import supercr.workflows.codereview.components.changeSummary
import supercr.workflows.codereview.components.titleAndDescription

external interface ChangesetOverviewScreenProps: RProps {
    var fileDiffList: FileDiffList
    var pullRequestSummary: PullRequestSummary
    var handleStartReview: () -> Unit
}

external interface ChangeSetOverviewScreenState: RState {
}

/**
 * Main Screen to start off reviews with.
 * Should aim to show
 * (a) Summary of the PR
 * (b) Files changed etc
 * (c) Stats
 * (d) Comments
 */
class ChangesetOverviewScreen : RComponent<ChangesetOverviewScreenProps, ChangeSetOverviewScreenState>() {

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
                                onSelected = props.handleStartReview
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
        }
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

}

fun RBuilder.changeSetOverviewScreen(handler: ChangesetOverviewScreenProps.() -> Unit): ReactElement {
    return child(ChangesetOverviewScreen::class) {
        this.attrs(handler)
    }
}
