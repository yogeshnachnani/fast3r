package supercr.views

import AceEditor
import Grid
import codereview.DiffEditType
import codereview.Edit
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import supercr.css.TextStyles

external interface DiffViewProps: RProps {
    var branchName: String
    var oldText: String
    var newText: String
    var editList: List<Edit>
}
external interface DiffViewState: RState {
    var currentFileName: String
}

class DiffView: RComponent<DiffViewProps, DiffViewState>() {
    val ace = js("require('ace-builds/src-noconflict/ace')")
    val webpackResolver = js("require('ace-builds/webpack-resolver')")
    val theme = js("require('ace-builds/src-noconflict/theme-solarized_light')")
    val split = js("require('ace-builds/src-noconflict/ext-split')")
    private var leftEditor: dynamic = null
    private var rightEditor: dynamic = null

    override fun RBuilder.render() {
        Grid {
            attrs {
                item = false
                container = true
                alignItems = "center"
                direction = "row"
                justify = "center"
                spacing = 2
            }
            Grid {
                attrs {
                    item = true
                    container = false
                }
                AceEditor {
                    attrs {
                        mode = "java"
                        theme = "github"
                        name = "left-main-editor"
                        readOnly = true
                        value = props.oldText
                    }
                }
            }
            Grid {
                attrs {
                    item = true
                    container = false
                }
                AceEditor {
                    attrs {
                        mode = "java"
                        theme = "github"
                        name = "right-main-editor"
                        readOnly = true
                        value = props.newText
                    }
                }
            }
        }
    }

    override fun componentDidMount() {
        leftEditor = ace.edit("left-main-editor")
        rightEditor = ace.edit("right-main-editor")

        /**
         * Process the [props.editList] to highlight the diff in oth the editors
         */
        props.editList.fold(initial = 0L) { numLinesAddedToRightEditor, currentEdit ->
            /**
             * Basically, we base all our highlights with the 'right hand side' offsets.
             * Which means that if we want to highlight the 'changed' lines on the left hand side of the diff,
             * we derive the line numbers using the 'right hand side' - [Edit.beginB] as the offset.
             * Of course, the [Edit] list line numbers are of the original source.
             * While showing the diff, we may insert some lines on the right hand side as well (in the case of deleted lines, for eg)
             * Which means we need to keep a track of how many lines we've inserted in the right side, which is captured in the accumulator here
             */
            currentEdit.processForOldEditor(numLinesAddedToRightEditor)
            val numLinesAddedForCurrentEdit = currentEdit.processForNewEditor(numLinesAddedToRightEditor)
            numLinesAddedToRightEditor + numLinesAddedForCurrentEdit
        }
//        Split(document.getElementById("main-editor")!!, "ace/theme/solarized_light", 2)
    }
    private fun Edit.processForNewEditor(offset: Long): Long {
        return when(editType) {
            DiffEditType.INSERT,
            DiffEditType.REPLACE -> {
                highlighLinesWithGutter(editor = rightEditor, fromRow = beginB + offset, numLines = lengthB, cssClazz = TextStyles.insertedTextNew)
                0L
            }
            DiffEditType.DELETE -> {
                val numLinesInDocument = rightEditor.getSession().getLength() as Number
                if (numLinesInDocument.toLong() - 1 <= endB  )  {
                    /** Handle deletions at end of file */
                    insertEmptyLinesAt(editor = rightEditor ,rowNumber = numLinesInDocument.toLong(),  numLines = lengthB + 1)
                    highlighLinesWithGutter(rightEditor, numLinesInDocument.toLong(), lengthB + 1, TextStyles.textInsertedForBalance)
                    0L
                } else {
                    /** We are not at the end of file */
                    insertEmptyLinesAt(editor = rightEditor ,rowNumber = beginB + offset,  numLines = lengthA)
                    highlighLinesWithGutter(rightEditor, beginB + offset, lengthA, TextStyles.textInsertedForBalance)
                    lengthA
                }
            }
            DiffEditType.EMPTY -> TODO()
        }
    }
    private fun Edit.processForOldEditor(offset: Long) {
        /**
         * The region highlighted by [beginA] to [endA] has been 'replaced' _in the original (left side) document
         * Note that if there were any INSERTS or REPLACES in the [leftEditor] before this, then those would have
         * brought the 'document' lines in the [leftEditor] at par with lines in [rightEditor]
         * Thus, we always use [beginB] as the starting point of the region to highlight
         *
         */
        when(editType) {
            DiffEditType.REPLACE -> {
                /** TODO: Need to process line diff */
                highlighLinesWithGutter(leftEditor, beginB + offset, lengthA, TextStyles.removedText)
                /** If there is new text on the right hand side, that means we have to fill empty lines */
                if (lengthB > lengthA) {
                    val numLines = lengthB - lengthA
                    insertEmptyLinesAt(editor = leftEditor ,rowNumber = beginB + offset + lengthA, numLines = numLines)
                    highlighLinesWithGutter(leftEditor, beginB + offset + lengthA, numLines, TextStyles.textInsertedForBalance)
                }
            }
            DiffEditType.DELETE -> {
                /** [beginA] and [endA] denote the line numbers of the _old_ text
                 * We must use the line numbers of the new text - denoted by [beginB] and [endB] to
                 * highlight the part that was _removed_ from old text
                 */
                highlighLinesWithGutter(leftEditor, beginB + offset, lengthA, TextStyles.removedText)
            }
            DiffEditType.INSERT -> {
                val numLines = lengthB - lengthA
                val numLinesInDocument = leftEditor.getSession().getLength() as Number
                console.log("Handling INSERT for old editor. Will insert $numLines at ${beginB + offset}. Num of current lines is $numLinesInDocument")
                insertEmptyLinesAt(editor = leftEditor, rowNumber = beginB + offset, numLines = numLines)
                highlighLinesWithGutter(leftEditor, beginB + offset, numLines, TextStyles.textInsertedForBalance)
            }
            DiffEditType.EMPTY -> TODO()
        }
    }

    private fun highlighLinesWithGutter(editor: dynamic, fromRow: Long, numLines: Long, cssClazz: String) {
        editor.getSession().highlightLines(fromRow.toDouble(), ( fromRow + numLines - 1 ) .toDouble(), cssClazz)
        (fromRow until (fromRow + numLines)).map { rowNumber ->
            editor.getSession().addGutterDecoration(rowNumber.toDouble() , cssClazz)
        }
    }

    private fun insertEmptyLinesAt(editor: dynamic, rowNumber: Long,numLines: Long) {
        val emptyLinesArray = generateEmptyLineArray(numLines)
        editor.getSession().getDocument().insertLines(rowNumber, emptyLinesArray)
    }

    private fun generateEmptyLineArray(numLines: Long) = Array<String>(numLines.toInt()) { i -> "" }

}

fun RBuilder.diffView(handler: DiffViewProps.() -> Unit): ReactElement {
    return child(DiffView::class) {
        this.attrs(handler)
    }
}

