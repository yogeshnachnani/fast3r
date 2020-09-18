package io.btc.supercr.git.processor

import codereview.DiffChangeType
import codereview.DiffEditType
import codereview.Edit
import codereview.FileData
import codereview.FileDiffV2
import codereview.FileLine
import codereview.FilePatchType
import codereview.FileTShirtSize
import codereview.SimpleFileDiff
import jsonParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.patch.FileHeader

/**
 *
 */
fun DiffFormatter.process(diffEntry: DiffEntry, oldFileText: String?, newFileText: String?): FileDiffV2 {
    val fileHeader = this.toFileHeader(diffEntry)
    val edits = fileHeader.toEditList()
        .map { edit ->
            Edit(
                beginA = edit.beginA.toLong(),
                beginB = edit.beginB.toLong(),
                endA = edit.endA.toLong(),
                endB = edit.endB.toLong()
            )
        }
    val (linesForOldText, linesForNewText) = if (fileHeader.patchType == FileHeader.PatchType.BINARY) {
        convertTextToFileLines(oldFileText, newFileText)
    } else {
        edits.processWith(oldFileText, newFileText)
    }
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
        editList = edits,
        patchType = if (fileHeader.patchType == FileHeader.PatchType.BINARY || fileHeader.patchType == FileHeader.PatchType.GIT_BINARY) {
            FilePatchType.BINARY
        } else {
            FilePatchType.TEXT
        }
    )
}

fun List<Edit>.processWith(oldFileText: String?, newFileText: String?): Pair<List<FileLine>, List<FileLine>> {
    val (oldFileLines, newFileLines) = convertTextToFileLines(oldFileText, newFileText)
    return if (oldFileLines.isEmpty() || newFileLines.isEmpty()) {
        Pair(oldFileLines, newFileLines)
    } else {
        processDiff(oldFileLines, newFileLines)
    }
}

private fun convertTextToFileLines(oldFileText: String?, newFileText: String?): Pair<List<FileLine>, List<FileLine>> {
    val oldFileLines = oldFileText?.lines() ?: listOf()
    val newFileLines = newFileText?.lines() ?: listOf()
    require(oldFileLines.isNotEmpty() || newFileLines.isNotEmpty())
    return if (oldFileLines.isEmpty() || newFileLines.isEmpty()) {
        Pair(
            oldFileLines.mapIndexed { index, line -> FileLine(lineText = line, filePosition = index, lineItems = listOf()) },
            newFileLines.mapIndexed { index, line -> FileLine(lineText = line, filePosition = index, lineItems = listOf())}
        )
    } else {
        Pair(
            oldFileLines.mapIndexed { index, line -> FileLine(lineText = line, filePosition = index, lineItems = listOf()) },
            newFileLines.mapIndexed { index, line -> FileLine(lineText = line, filePosition = index, lineItems = listOf())}
        )
    }
}

private fun List<Edit>.processDiff(oldFileLines: List<FileLine>, newFileLines: List<FileLine>): Pair<List<FileLine>, List<FileLine>> {
    val generatedOldFileLineData = oldFileLines.toMutableList()
    val generatedNewFileLineData= newFileLines.toMutableList()
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
            when {
                lengthB > lengthA -> {
                    /** If there is new text on the right hand side, that means we have to fill empty lines */
                    /** If there is new text on the right hand side, that means we have to fill empty lines */
                    val numLines = lengthB - lengthA
                    oldFileLineData.insertEmptyLinesAt(rowNumber = beginB + offset + lengthA, numLines = numLines)
                    0L
                }
                lengthA > lengthB -> {
                    /** This is basically equivalent to a DELETE since there are more lines in oldText than in newText */
                    /** This is basically equivalent to a DELETE since there are more lines in oldText than in newText */
                    val numLines = lengthA - lengthB
                    newFileLineData.insertEmptyLinesAt(rowNumber = endB + offset, numLines = numLines)
                    numLines
                }
                else -> {
                    0L
                }
            }
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

/**
 * Hacky way of generating json for a given diff so that we can write tests for it easily
 */
private fun writeTestJson(edits: List<Edit>, oldFileText: String?, newFileText: String?): String {
    return jsonParser.encodeToString(SimpleFileDiff.serializer(), SimpleFileDiff(oldFileText, newFileText, edits))
}

