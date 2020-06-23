package supercr.workflows.codereview.components

import Paper
import codereview.FileDiffListV2
import codereview.FileDiffV2
import codereview.FileTShirtSize
import codereview.FileTShirtSize.L
import codereview.FileTShirtSize.M
import codereview.FileTShirtSize.S
import codereview.FileTShirtSize.XL
import codereview.FileTShirtSize.XS
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.div
import react.dom.p
import styled.getClassName
import styled.styled
import styled.styledP
import supercr.css.ComponentStyles

external interface ChangeSummaryProps : RProps {
    var fileDiffList: FileDiffListV2
}

external interface ChangeSummaryState : RState {

}

class ChangeSummary : RComponent<ChangeSummaryProps, ChangeSummaryState>() {
    override fun RBuilder.render() {
        val fileDiffs = props.fileDiffList.fileDiffs
        Paper {
            attrs {
                square = true
                variant = "outlined"
                className = ComponentStyles.getClassName { ComponentStyles::infoPaper }
            }
            p {
                + fileDiffs.filesChangedDescription()
            }
            fileChangesByTshirtSize()
        }
    }

    private fun RBuilder.fileChangesByTshirtSize() {
        val tshirtSizes = listOf(XS, S, M, L, XL)
        div {
            tshirtSizes.map { tshirtSize ->
                val filesOfSize = props.fileDiffList.fileDiffs.filter { it.tShirtSize == tshirtSize }
                if (filesOfSize.isNotEmpty()) {
                    p {
                        + filesOfSize.fileSizeDescription(tshirtSize)
                    }
                }
            }
        }
    }

    private fun List<FileDiffV2>.fileSizeDescription(fileTShirtSize: FileTShirtSize): String {
        return "    $size ${fileTShirtSize.name} ${if (size > 1) {"files"} else { "file"}}"
    }

    private fun List<FileDiffV2>.filesChangedDescription(): String {
        return "$size ${if (size > 1) {"files"} else { "file"}} changed"
    }
}

fun RBuilder.changeSummary(handler: ChangeSummaryProps.() -> Unit): ReactElement {
    return child(ChangeSummary::class) {
        this.attrs(handler)
    }
}