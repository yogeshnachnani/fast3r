package supercr.workflows.codereview.processor

import codereview.FileData
import codereview.FileDiffV2
import codereview.hasNewFile
import codereview.hasOldFile

fun FileDiffV2.hasBothFiles(): Boolean {
    return hasOldFile() && hasNewFile()
}

fun FileDiffV2.nextEditPosition(currentEditIndex: Int): Pair<Int?, Int?> {
    return if (currentEditIndex == editList.lastIndex || newFile == null) {
        Pair(null, null)
    } else {
        Pair(currentEditIndex + 1, newFile.getViewPositionForFilePosition(editList[currentEditIndex+1].beginB.toInt()))
    }
}

private fun FileData.getViewPositionForFilePosition(givenFilePosition: Int): Int {
    return fileLines.indexOfFirst { it.filePosition == givenFilePosition }
}