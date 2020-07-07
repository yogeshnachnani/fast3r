package supercr.workflows.codereview.processor

import Range
import codereview.DiffEditType
import codereview.Edit
import codereview.FileLine
import styled.getClassName
import supercr.css.ComponentStyles

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
        editList
            .filter { it.editType == DiffEditType.REPLACE }
            .also { console.log("Processing ${it.size} REPLACE edits from $editList") }
            .forEach { currentEdit ->
                currentEdit.processEdit()
            }
    }

    fun highlightLinesAddedForBalance(oldFileLines: List<FileLine>, newFileLines: List<FileLine>) {
        oldFileLines
            .mapIndexed { index, fileLine -> Pair(index.toLong(), fileLine) }
            .filter { it.second.filePosition == null }
            .also { console.log("Will highlight ${it.map { it.first }} lines in the old editor") }
            .forEach { (rowIndex, rowAddedInOldText) ->
                highlighLinesWithGutter(editor = editorWithOldText, fromRow = rowIndex, numLines = 1, cssClazz = ComponentStyles.getClassName { ComponentStyles::diffViewTextAddedForBalance })
                highlighLinesWithGutter(editor = editorWithNewText, fromRow = rowIndex, numLines = 1, cssClazz = ComponentStyles.getClassName { ComponentStyles::diffViewNewText })
            }
        newFileLines
            .mapIndexed { index, fileLine -> Pair(index.toLong(), fileLine) }
            .filter { it.second.filePosition == null }
            .also { console.log("Will highlight ${it.map { it.first }} lines in the new editor") }
            .forEach { (rowIndex, rowAddedInNewText) ->
                highlighLinesWithGutter(editorWithNewText, rowIndex, 1, ComponentStyles.getClassName { ComponentStyles::diffViewTextAddedForBalance })
                highlighLinesWithGutter(editorWithOldText, rowIndex, 1, ComponentStyles.getClassName { ComponentStyles::diffViewDeletedText } )
            }
    }

    private fun Edit.processEdit() {
        console.log("Processing edit : $this")
        require(editType == DiffEditType.REPLACE)
        /** If there is new text on the right hand side, that means we have to highlight the empty lines we inserted in oldText for balance */
        if (lengthB > lengthA) {
            val numLines = lengthB - lengthA
            highlighLinesWithGutter(editorWithOldText, beginB + lengthA, numLines, ComponentStyles.getClassName { ComponentStyles::diffViewTextAddedForBalance })
        }
        /** We have the same number or rows to process in both [editorWithOldText] and [editorWithNewText] */
        val fromRow = beginB
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
                                HighlightType.TextAdded -> ComponentStyles.getClassName { ComponentStyles::diffViewNewText }
                                HighlightType.TextRemoved -> ComponentStyles.getClassName { ComponentStyles::diffViewDeletedText }
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
                                HighlightType.TextAdded -> ComponentStyles.getClassName { ComponentStyles::diffViewNewText }
                                HighlightType.TextRemoved -> ComponentStyles.getClassName { ComponentStyles::diffViewDeletedText }
                            }
                        )
                    }
                }

        }
        /** Finally, highlight the gutters */
        highlightGutter(editor = editorWithNewText, fromRow = beginB , numLines = lengthB, cssClazz = ComponentStyles.getClassName { ComponentStyles::diffViewNewText })
        highlightGutter(editor = editorWithOldText, fromRow = beginB , numLines = lengthA, cssClazz = ComponentStyles.getClassName { ComponentStyles::diffViewDeletedText })
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

}