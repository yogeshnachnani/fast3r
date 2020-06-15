package supercr.components

import Avatar
import codereview.FileTShirtSize
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import styled.getClassName
import supercr.css.AvatarSize
import supercr.css.ComponentStyles


external interface FileSizeChipComponentProps : RProps {
    var fileSize: FileTShirtSize
    var avatarSize: AvatarSize
}

external interface FileSizeChipComponentState : RState {

}

class FileSizeChipComponent : RComponent<FileSizeChipComponentProps, FileSizeChipComponentState>() {
    override fun RBuilder.render() {
        Avatar {
            attrs {
                variant = "square"
                className = when(props.avatarSize) {
                    AvatarSize.tiny -> ComponentStyles.getClassName { ComponentStyles::tinyTextAvatar }
                    AvatarSize.small -> ComponentStyles.getClassName { ComponentStyles::smallTextAvatar }
                }
            }
            + props.fileSize.name
        }
    }
}

fun RBuilder.fileSizeChip(handler: FileSizeChipComponentProps.() -> Unit): ReactElement {
    return child(FileSizeChipComponent::class) {
        this.attrs(handler)
    }
}