package supercr.workflows.codereview.processor

import codereview.FileData
import codereview.FileDiffV2

fun FileDiffV2.enableHunkTraversal(): Boolean {
    return oldFile != null && newFile != null
}

fun FileDiffV2.getNextHunk(viewPositionLeft: Number, viewPositionRight: Number): Int? {
    val leftEdit = editList.firstOrNull { it.beginA > viewPositionLeft.toLong() }
    val rightEdit = editList.firstOrNull { it.beginB > viewPositionLeft.toLong() }
    return when {
        leftEdit == null && rightEdit == null -> null
        leftEdit == null ||
        leftEdit == rightEdit -> newFile!!.getViewPositionForFilePosition(rightEdit!!.beginB.toInt())
        rightEdit == null ||
        leftEdit.beginA < rightEdit.beginB -> oldFile!!.getViewPositionForFilePosition(leftEdit.beginB.toInt())
        else -> newFile!!.getViewPositionForFilePosition(rightEdit.beginB.toInt())
    }
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