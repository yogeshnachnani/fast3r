package io.btc.supercr.git.processor

import codereview.DiffChangeType
import codereview.DiffEditType
import codereview.Edit
import codereview.FileData
import codereview.FileDiffV2
import codereview.FileLine
import codereview.FileTShirtSize
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter

/**
 *
 */
fun DiffFormatter.process(diffEntry: DiffEntry, oldFileText: String?, newFileText: String?): FileDiffV2 {
    val edits = this.toFileHeader(diffEntry).toEditList()
        .map { edit ->
            Edit(
                beginA = edit.beginA.toLong(),
                beginB = edit.beginB.toLong(),
                endA = edit.endA.toLong(),
                endB = edit.endB.toLong()
            )
        }
    val (linesForOldText, linesForNewText) = edits.processWith(oldFileText, newFileText)
    return FileDiffV2(
        oldFile = FileData(
            objectId = diffEntry.oldId.name() ,
            fileLines = linesForOldText,
            path = diffEntry.oldPath
        ),
        newFile = FileData(
            objectId = diffEntry.newId.name(),
            fileLines = linesForNewText,
            path = diffEntry.newPath
        ),
        diffChangeType = when(diffEntry.changeType) {
            DiffEntry.ChangeType.ADD -> DiffChangeType.ADD
            DiffEntry.ChangeType.MODIFY -> DiffChangeType.MODIFY
            DiffEntry.ChangeType.DELETE -> DiffChangeType.DELETE
            DiffEntry.ChangeType.RENAME -> DiffChangeType.RENAME
            DiffEntry.ChangeType.COPY -> DiffChangeType.COPY
            else -> throw RuntimeException("Not possible to have a null change type for file ${diffEntry.newPath}")
        },
        tShirtSize = edits.fold(0L) {acc, edit ->
            acc + edit.lengthB
        }.let { totalEditLength ->
            when {
                totalEditLength < 2L -> FileTShirtSize.XS
                totalEditLength < 60L -> FileTShirtSize.S
                totalEditLength < 100L -> FileTShirtSize.M
                totalEditLength < 160L -> FileTShirtSize.L
                else -> FileTShirtSize.XL
            }
        },
        editList = edits
    )
}

fun List<Edit>.processWith(oldFileText: String?, newFileText: String?): Pair<List<FileLine>, List<FileLine>> {
    val oldFileLines = oldFileText?.lines() ?: listOf()
    val newFileLines = newFileText?.lines() ?: listOf()
    require(oldFileLines.isNotEmpty() || newFileLines.isNotEmpty())
    return if (oldFileLines.isEmpty() || newFileLines.isEmpty()) {
        Pair(
            oldFileLines.mapIndexed { index, line -> FileLine(lineText = line, filePosition = index, lineItems = listOf()) },
            newFileLines.mapIndexed { index, line -> FileLine(lineText = line, filePosition = index, lineItems = listOf())}
        )
    } else {
        processDiff(oldFileLines, newFileLines)
    }
}

private fun List<Edit>.processDiff(oldFileLines: List<String>, newFileLines: List<String>): Pair<List<FileLine>, List<FileLine>> {
    val generatedOldFileLineData = oldFileLines
        .mapIndexed { index, line -> FileLine(lineText = line, filePosition = index, lineItems = listOf()) }
        .toMutableList()
    val generatedNewFileLineData= newFileLines
        .mapIndexed { index, line -> FileLine(lineText = line, filePosition = index, lineItems = listOf())}
        .toMutableList()
    this.fold(initial = 0L) { totalLinesAddedToNewFile, currentEdit ->
        val numLinesAddedForCurrentEdit = currentEdit.processEdit(totalLinesAddedToNewFile, generatedOldFileLineData, generatedNewFileLineData)
        totalLinesAddedToNewFile + numLinesAddedForCurrentEdit
    }
    return Pair(generatedOldFileLineData, generatedNewFileLineData)
}

private fun Edit.processEdit(offset: Long, oldFileLineData: MutableList<FileLine>, newFileLineData: MutableList<FileLine>): Long {
    return when(editType) {
        DiffEditType.INSERT -> {
            /** Process For OldFile */
            val numLines = lengthB - lengthA
            oldFileLineData.insertEmptyLinesAt(rowNumber = beginB + offset, numLines = numLines)
            /** Nothing to do for NewFile since the lines marked by beginB to endB are already present */
            0L
        }
        DiffEditType.DELETE -> {
            /** Nothing to do for OldFile since the lines that are _deleted_ are already present in [oldFileLineData]  */
            if (newFileLineData.size - 1 <= endB  )  {
                /** Handle deletions at end of file */
                newFileLineData.insertEmptyLinesAt(rowNumber = newFileLineData.size.toLong(),  numLines = lengthB + 1)
                0L
            } else {
                newFileLineData.insertEmptyLinesAt(rowNumber = beginB + offset, numLines = lengthA)
                lengthA
            }
        }
        DiffEditType.REPLACE -> {
            /** If there is new text on the right hand side, that means we have to fill empty lines */
            if (lengthB > lengthA) {
                val numLines = lengthB - lengthA
                oldFileLineData.insertEmptyLinesAt(rowNumber = beginB + offset + lengthA, numLines = numLines)
            }
            0L
        }
        DiffEditType.EMPTY -> TODO()
    }
}

private fun MutableList<FileLine>.insertEmptyLinesAt(rowNumber: Long, numLines: Long) {
    (0 until numLines).map { _ ->
        FileLine(lineText = "", filePosition = null, lineItems = listOf())
    }
        .also {
            this.addAll(rowNumber.toInt(), it)
        }
}
