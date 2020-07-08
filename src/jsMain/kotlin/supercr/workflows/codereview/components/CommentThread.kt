package supercr.workflows.codereview.components

import ListItem
import MaterialUIList
import Paper
import codereview.FileLineItem
import kotlinx.css.FontWeight
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.marginBottom
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.width
import org.w3c.dom.events.Event
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
import styled.styledP
import supercr.css.ComponentStyles
import supercr.css.FontSizes
import supercr.kb.components.ctrlEnterInput
import supercr.utils.iso8601ToHuman

external interface CommentThreadProps : RProps {
    var comments: List<FileLineItem.Comment>
    var newComments: List<FileLineItem.Comment>
    var onCommentAdd: (String) -> Unit
    var hideMe: () -> Unit
}

external interface CommentThreadState : RState {
    var showCommentInput: Boolean
}

class CommentThread(
    constructorProps: CommentThreadProps
) : RComponent<CommentThreadProps, CommentThreadState>(constructorProps) {
    override fun RBuilder.render() {
        Paper {
            attrs {
                className = ComponentStyles.getClassName { ComponentStyles::commentPaper }
                elevation = 6
                variant = "outlined"
            }
            MaterialUIList {
                attrs {
                    className = ComponentStyles.getClassName { ComponentStyles::compactCommentListItem }
                }
                props.comments
                    .plus(props.newComments)
                    .mapIndexed { index, comment -> showComment(comment, index == props.comments.lastIndex) }
                if (state.showCommentInput || props.comments.isEmpty()) {
                    renderCommentInput()
                }
            }
        }
    }

//    override fun CommentThreadState.init(props: CommentThreadProps) {
//        showCommentInput = props.comments.isEmpty()
//    }

    private val showCommentInputBox : () -> Unit = {
        setState {
            showCommentInput = true
        }
    }

    private val discardComment: () -> Unit = {
        setState {
            showCommentInput = false
        }
        if (props.comments.isEmpty()) {
            /** This will unmount this component */
            props.hideMe()
        }
    }

    private val handleCommentAddition: (String) -> Unit = { commentBody ->
        props.onCommentAdd(commentBody)
        forceUpdate()
    }

    private fun RBuilder.renderCommentInput(): ReactElement {
        return ListItem {
            attrs {
                button = false
                divider = false
                className = ComponentStyles.getClassName { ComponentStyles::compactCommentListItem }
            }
            ctrlEnterInput {
                className = ComponentStyles.getClassName { ComponentStyles::commentInputBox }
                rows = 1
                rowsMax = 5
                placeholder = "Comment.. "
                onInputCtrlEnter = handleCommentAddition
                onEscape = discardComment
            }
        }
    }

    private fun RBuilder.showComment(comment: FileLineItem.Comment, isLastItem: Boolean): ReactElement {
        return ListItem {
            attrs {
                button = true
                divider = !isLastItem
                onClick = showCommentInputBox
                className = ComponentStyles.getClassName { ComponentStyles::compactCommentListItem }
            }
            styledDiv {
                css {
                    width = 100.pct
                    marginBottom = 6.px
                }
                styledP {
                    css {
                        fontSize = FontSizes.normal
                        fontWeight = FontWeight.w700
                        marginTop = 5.px
                        marginLeft = 5.px
                        marginRight = 0.px
                        marginBottom = 0.px
                    }
                    + comment.userId
                }
                styledP {
                    css {
                        fontSize = FontSizes.tiny
                        marginTop = 0.px
                        marginLeft = 5.px
                    }
                    + comment.updatedAt.iso8601ToHuman()
                }
            }
            styledDiv {
                css {
                    width = 100.pct
                    marginTop = 6.px
                    marginBottom = 5.px
                }
                styledP {
                    css {
                        marginTop = 5.px
                        fontSize = FontSizes.small
                        marginLeft = 5.px
                        marginRight = 0.px
                        marginTop = 0.px
                    }
                    + comment.body
                }
            }
        }
    }
}

fun RBuilder.commentThread( handler: RElementBuilder<CommentThreadProps>.() -> Unit): ReactElement {
    return child(CommentThread::class) {
        handler()
    }
}