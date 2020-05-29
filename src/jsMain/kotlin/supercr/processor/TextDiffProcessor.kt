package supercr.processor

import Range
import codereview.DiffEditType
import codereview.Edit
import supercr.css.TextStyles

/**
 * Basically used to create appropriate decorations in our text editors
 */
class TextDiffProcessor constructor(
    private val editorWithOldText: dynamic,
    private val editorWithNewText: dynamic
) {
    /**
     * Process the [editList] to highlight the diff in oth the editors
     */
    fun processEditList(editList: List<Edit>) {
        editList.fold(initial = 0L) { numLinesAddedToRightEditor, currentEdit ->
            /**
             * Basically, we base all our highlights with the 'right hand side' offsets.
             * Which means that if we want to highlight the 'changed' lines on the left hand side of the diff,
             * we derive the line numbers using the 'right hand side' - [Edit.beginB] as the offset.
             * Of course, the [Edit] list line numbers are of the original source.
             * While showing the diff, we may insert some lines on the right hand side as well (in the case of deleted lines, for eg)
             * Which means we need to keep a track of how many lines we've inserted in the right side, which is captured in the accumulator here
             *
             * Note: Given the logic explained above, it is important that we _always_ process [editorWithOldText] and then [editorWithNewText]
             */
            val numLinesAddedForCurrentEdit = currentEdit.processEdit(numLinesAddedToRightEditor)
//            currentEdit.processForOldEditor(numLinesAddedToRightEditor, editorWithOldText)
//            val numLinesAddedForCurrentEdit = currentEdit.processForNewEditor(numLinesAddedToRightEditor, editorWithNewText)
            numLinesAddedToRightEditor + numLinesAddedForCurrentEdit
        }
    }

    private fun Edit.processEdit(offset: Long): Long {
        /**
         * The region highlighted by [Edit.beginA] to [Edit.endA] has been 'replaced' _in the original "text" document
         * Note that if there were any INSERTS or REPLACES in the [editorWithOldText] before this, then those would have
         * brought the 'document' lines in the [editorWithOldText] at par with lines in [editorWithNewText]
         * Thus, we always use [Edit.beginB] as the starting point of the region to highlight in all of the following methods
         *
         */
        return when(editType) {
            DiffEditType.INSERT -> {
                processInsertForOldEditor(offset)
                processInsertForNewEditor(offset)
                0L
            }
            DiffEditType.DELETE -> {
                processDeleteForOldEditor(offset)
                processDeleteForNewEditor(offset)
            }
            DiffEditType.REPLACE -> {
                /** If there is new text on the right hand side, that means we have to fill empty lines */
                if (lengthB > lengthA) {
                    val numLines = lengthB - lengthA
                    insertEmptyLinesAt(editor = editorWithOldText, rowNumber = beginB + offset + lengthA, numLines = numLines)
                    highlighLinesWithGutter(editorWithOldText, beginB + offset + lengthA, numLines, TextStyles.textInsertedForBalance)
                }
                /** At this point, we have the same number or rows to process in both [editorWithOldText] and [editorWithNewText] */
                val fromRow = beginB + offset
                val upTillRow = fromRow + lengthB
                (fromRow until upTillRow).map { rowIndex ->
                    val oldText = getLineAt(rowIndex, editorWithOldText)
                    val newText = getLineAt(rowIndex, editorWithNewText)
                    LineDiffProcessor.getDiffMarkers(oldText, newText)
                        .let { (markersForOldTextEditor, markersForNewTextEditor) ->
                            // TODO : If the diff marker starts at column '0', then we highlight the section starting with column 0. This leaves a small gap between the gutter and the starting of the highlight. Fix that
                            markersForOldTextEditor.map { marker ->
                                highlightLineSection(
                                    rowIndex = rowIndex,
                                    fromColumn = marker.fromColumn,
                                    toColumn = marker.toColumn,
                                    editor = editorWithOldText,
                                    cssClazz = when(marker.highlightType) {
                                        HighlightType.TextAdded -> TextStyles.insertedTextNew
                                        HighlightType.TextRemoved -> TextStyles.removedText
                                    }
                                )
                            }
                            markersForNewTextEditor.map { marker ->
                                highlightLineSection(
                                    rowIndex = rowIndex,
                                    fromColumn = marker.fromColumn,
                                    toColumn = marker.toColumn,
                                    editor = editorWithNewText,
                                    cssClazz = when(marker.highlightType) {
                                        HighlightType.TextAdded -> TextStyles.insertedTextNew
                                        HighlightType.TextRemoved -> TextStyles.removedText
                                    }
                                )
                            }
                        }

                }
                /** Finally, highlight the gutters */
                highlightGutter(editor = editorWithNewText, fromRow = beginB + offset, numLines = lengthB, cssClazz = TextStyles.insertedTextNew)
                highlightGutter(editor = editorWithOldText, fromRow = beginB + offset, numLines = lengthA, cssClazz = TextStyles.removedText)
                0L
            }
            DiffEditType.EMPTY -> TODO()
        }
    }

    private fun getLineAt(rowIndex: Long, editor: dynamic): String {
        return ( editor.getSession().getLine(rowIndex.toDouble()) as String )
    }

    private fun highlightLineSection(rowIndex: Long, fromColumn: Long, toColumn: Long, editor: dynamic, cssClazz: String) {
        editor.getSession().addMarker(
            Range(rowIndex.toDouble(), fromColumn.toDouble(), rowIndex.toDouble(), toColumn.toDouble()),
            cssClazz,
            "text",
            false
        )
    }

    private fun Edit.processInsertForOldEditor(offset: Long) {
        val numLines = lengthB - lengthA
        insertEmptyLinesAt(editor = editorWithOldText, rowNumber = beginB + offset, numLines = numLines)
        highlighLinesWithGutter(editorWithOldText, beginB + offset, numLines, TextStyles.textInsertedForBalance)
    }

    private fun Edit.processInsertForNewEditor(offset: Long) {
        highlighLinesWithGutter(editor = editorWithNewText, fromRow = beginB + offset, numLines = lengthB, cssClazz = TextStyles.insertedTextNew)
    }

    private fun Edit.processDeleteForNewEditor(offset: Long): Long {
        val numLinesInDocument = editorWithNewText.getSession().getLength() as Number
        return if (numLinesInDocument.toLong() - 1 <= endB  )  {
            /** Handle deletions at end of file */
            insertEmptyLinesAt(editor = editorWithNewText ,rowNumber = numLinesInDocument.toLong(),  numLines = lengthB + 1)
            highlighLinesWithGutter(editorWithNewText, numLinesInDocument.toLong(), lengthB + 1, TextStyles.textInsertedForBalance)
            0L
        } else {
            /** We are not at the end of file */
            insertEmptyLinesAt(editor = editorWithNewText ,rowNumber = beginB + offset,  numLines = lengthA)
            highlighLinesWithGutter(editorWithNewText, beginB + offset, lengthA, TextStyles.textInsertedForBalance)
            lengthA
        }
    }

    private fun Edit.processDeleteForOldEditor(offset: Long) {
        /** [Edit.beginA] and [Edit.endA] denote the line numbers of the _old_ text
         * We must use the line numbers of the new text - denoted by [Edit.beginB] and [Edit.endB] to
         * highlight the part that was _removed_ from old text
         */
        highlighLinesWithGutter(editorWithOldText, beginB + offset, lengthA, TextStyles.removedText)
    }

    private fun highlighLinesWithGutter(editor: dynamic, fromRow: Long, numLines: Long, cssClazz: String) {
        highlightLines(editor, fromRow, numLines, cssClazz)
        highlightGutter(editor, fromRow, numLines, cssClazz)
    }

    private fun highlightLines(editor: dynamic, fromRow: Long, numLines: Long, cssClazz: String) {
        editor.getSession().highlightLines(fromRow.toDouble(), ( fromRow + numLines - 1 ) .toDouble(), cssClazz)
    }

    private fun highlightGutter(editor: dynamic ,fromRow: Long, numLines: Long, cssClazz: String) {
        (fromRow until (fromRow + numLines)).map { rowNumber ->
            editor.getSession().addGutterDecoration(rowNumber.toDouble() , cssClazz)
        }
    }

    private fun insertEmptyLinesAt(editor: dynamic, rowNumber: Long,numLines: Long) {
        val emptyLinesArray = generateEmptyLineArray(numLines)
        editor.getSession().getDocument().insertLines(rowNumber, emptyLinesArray)
    }

    private fun generateEmptyLineArray(numLines: Long) = Array(numLines.toInt()) { "" }

}