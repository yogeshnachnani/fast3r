package supercr.workflows.codereview.components

import ListItem
import MaterialUIList
import Paper
import codereview.FileLineItem
import kotlinx.css.FontWeight
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.margin
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
import supercr.utils.toDateTimeRepresentation

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
        styledDiv {
            css {
                + ComponentStyles.commentPaper
            }
            MaterialUIList {
                attrs {
                    className = ComponentStyles.getClassName { ComponentStyles::compactCommentListItem }
                }
                props.comments
                    .plus(props.newComments)
                    .mapIndexed { index, comment -> showComment(comment, index == props.comments.lastIndex) }
                if (state.showCommentInput) {
                    renderCommentInput()
                }
            }
        }
    }

    override fun CommentThreadState.init(props: CommentThreadProps) {
        showCommentInput = props.comments.isEmpty()
    }

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
        /** TODO(yogesh): Add this functionality once we implement "Escape" to hide comments/everything in [DiffView] */
//        setState {
//            showCommentInput = false
//        }
    }

    private fun RBuilder.renderCommentInput(): ReactElement {
        return ListItem {
            attrs {
                button = false
                divider = false
                className = "${ ComponentStyles.getClassName { ComponentStyles::compactCommentListItem } } ${ComponentStyles.getClassName { ComponentStyles::compactCommentListItemInputBox }}"
            }
            ctrlEnterInput {
                className = if (props.comments.isEmpty()) {
                    "${ComponentStyles.getClassName { ComponentStyles::commentInputBox }} ${ComponentStyles.getClassName { ComponentStyles::emptyThreadCommentInputBox }}"
                } else {
                    ComponentStyles.getClassName { ComponentStyles::commentInputBox }
                }
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
                divider = false
                onClick = showCommentInputBox
                className = ComponentStyles.getClassName { ComponentStyles::compactCommentListItem }
            }
            styledDiv {
                css {
                    + ComponentStyles.commentThreadContainer
                }
                styledP {
                    css {
                        + ComponentStyles.commentThreadUserId
                    }
                    + comment.userId
                }
                styledP {
                    css {
                        + ComponentStyles.commentThreadDateTime
                    }
                    + comment.updatedAt.toDateTimeRepresentation()
                }
                styledP {
                    css {
                        + ComponentStyles.commentThreadCommentBody
                    }
                    + comment.body
                }
                if (!isLastItem) {
                    styledDiv {
                        css {
                            + ComponentStyles.commentThreadSeparator
                        }
                    }
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