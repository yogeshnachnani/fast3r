package supercr.workflows.codereview.components

import Paper
import codereview.FileDiff
import codereview.FileDiffList
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
import react.dom.p
import styled.styled
import styled.styledP

external interface ChangeSummaryProps : RProps {
    var fileDiffList: FileDiffList
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
            }
            p {
                + fileDiffs.filesChangedDescription()
            }
            fileChangesByTshirtSize()
        }
    }

    private fun RBuilder.fileChangesByTshirtSize() {
        val tshirtSizes = listOf(XS, S, M, L, XL)
        p {
            tshirtSizes.map { tshirtSize ->
                val filesOfSize = props.fileDiffList.fileDiffs.filter { it.fileHeader.tShirtSize == tshirtSize }
                if (filesOfSize.isNotEmpty()) {
                    p {
                        + filesOfSize.fileSizeDescription(tshirtSize)
                    }
                }
            }
        }
    }

    private fun List<FileDiff>.fileSizeDescription(fileTShirtSize: FileTShirtSize): String {
        return "    $size ${fileTShirtSize.name} ${if (size > 1) {"files"} else { "file"}}"
    }

    private fun List<FileDiff>.filesChangedDescription(): String {
        return "$size ${if (size > 1) {"files"} else { "file"}} changed"
    }
}

fun RBuilder.changeSummary(handler: ChangeSummaryProps.() -> Unit): ReactElement {
    return child(ChangeSummary::class) {
        this.attrs(handler)
    }
}