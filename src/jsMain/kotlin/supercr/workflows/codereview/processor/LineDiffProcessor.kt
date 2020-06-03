package supercr.workflows.codereview.processor

import JsDiffResult
import diffWords

enum class HighlightType {
    TextAdded,
    TextRemoved
}

/**
 * Essentially stores 'marker' information to word/character level differences between two lines of text
 * The range [fromColumn] and [toColumn] is inclusive
 */
data class VimDiffRowMarker(
    val fromColumn: Long,
    val toColumn: Long,
    val highlightType: HighlightType
)

private enum class JsDiffType {
    Added,
    Removed,
    Same
}

class LineDiffProcessor {
    companion object {
        /**
         * For given two lines of text, this method returns a Pair of lists of [VimDiffRowMarker]
         * First part of the Pair is a list of markers to be applied to the editor displaying the [oldLine]
         * Second part of the Pair is a list of markers to be applied to the editor displaying the [newLine]
         * We could have returned a single list of markers as well, considering a [HighlightType.TextAdded] would never appear in the first part of the result pair
         * Similarly, a [HighlightType.TextRemoved] would never appear in the 2nd part of the pair.
         */
        fun getDiffMarkers(oldLine: String, newLine: String): Pair<List<VimDiffRowMarker>, List<VimDiffRowMarker>> {
            val diff = diffWords(oldLine, newLine)
            val initialAccumulator = Triple(0L, 0L, listOf<VimDiffRowMarker>())
            return diff.fold(initialAccumulator) { (oldTextIndex, newTextIndex, accumulatedMarkers), currentDiff ->
                when(currentDiff.getType()) {
                    JsDiffType.Added -> {
                        /** Move the index only newText and create a marker */
                        val newMarker = VimDiffRowMarker(fromColumn = newTextIndex, toColumn = newTextIndex + currentDiff.value.length - 1, highlightType = HighlightType.TextAdded)
                        Triple(oldTextIndex, newTextIndex + currentDiff.value.length, accumulatedMarkers.plus(newMarker))
                    }
                    JsDiffType.Removed -> {
                        /** Move the index only for oldText and create a marker */
                        val marker = VimDiffRowMarker(fromColumn = oldTextIndex, toColumn = oldTextIndex + currentDiff.value.length - 1, highlightType = HighlightType.TextRemoved)
                        Triple(oldTextIndex + currentDiff.value.length, newTextIndex, accumulatedMarkers.plus(marker))
                    }
                    JsDiffType.Same -> {
                        /** There won't be a new Marker - we just forward the indexes */
                        Triple(oldTextIndex + currentDiff.value.length, newTextIndex + currentDiff.value.length , accumulatedMarkers)
                    }
                }
            }.let { (oldTextIndex, newTextIndex, accumulatedMarkers) ->
                require(oldTextIndex.toInt() == oldLine.length) {
                    "For some reason, for the oldLine, We got only up to $oldTextIndex whereas we should have got up till ${oldLine.length}"
                }
                require(newTextIndex.toInt() == newLine.length) {
                    "For some reason, for the newLine, We got only up to $newTextIndex whereas we should have got up till ${newLine.length}"
                }
                Pair(
                    first = accumulatedMarkers.filter { it.highlightType == HighlightType.TextRemoved },
                    second = accumulatedMarkers.filter { it.highlightType == HighlightType.TextAdded }
                )
            }
        }

        private fun JsDiffResult.getType(): JsDiffType {
            return if(this.added == null && this.removed == null) {
                JsDiffType.Same
            } else if (this.added == true) {
                JsDiffType.Added
            } else {
                JsDiffType.Removed
            }
        }
    }
}