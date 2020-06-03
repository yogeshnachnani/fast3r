package supercr.workflows.codereview.processor

import kotlin.test.Test
import kotlin.test.assertEquals

class LineDiffProcessorTest {
    @Test
    fun testNewWordAddedToNewLine() {
        val oldLine = "System.out.println('This is 2nd line');"
        val newLine = "System.out.println('This is 2nd changed line');"
        val (expectedMarkersForOldText, expectedMarkersForNewText) = Pair<List<VimDiffRowMarker>, List<VimDiffRowMarker>>(
            emptyList(),
            listOf(
                VimDiffRowMarker(fromColumn = 32, toColumn = 39, highlightType = HighlightType.TextAdded)
            )
        )
        LineDiffProcessor.getDiffMarkers(oldLine, newLine)
            .let { (markersForOldText, markersForNewText) ->
                expectedMarkersForNewText.verify(markersForNewText)
                expectedMarkersForOldText.verify(markersForOldText)
            }
    }

    @Test
    fun testWordRemovedFromNewLine() {
        val oldLine = "System.out.println('This is 2nd line');"
        val newLine = "System.out.println('2nd line');"
        val expectedMarkersForNewText = emptyList<VimDiffRowMarker>()
        val expectedMarkersForOldText = listOf(
            VimDiffRowMarker(fromColumn = 20, toColumn = 27, highlightType = HighlightType.TextRemoved)
        )
        LineDiffProcessor.getDiffMarkers(oldLine, newLine)
            .let { (markersForOldText, markersForNewText) ->
                expectedMarkersForNewText.verify(markersForNewText)
                expectedMarkersForOldText.verify(markersForOldText)
            }
    }

    @Test
    fun testWordRemovedAndAddedToNewLine() {
        val oldLine = "System.out.println('This is 2nd line');"
        val newLine = "System.out.println('2nd line is that');"
        val expectedMarkersForNewText = listOf(
            VimDiffRowMarker(fromColumn = 20, toColumn = 22, highlightType = HighlightType.TextAdded),
            VimDiffRowMarker(fromColumn = 24, toColumn = 28, highlightType = HighlightType.TextAdded),
            VimDiffRowMarker(fromColumn = 32, toColumn = 35, highlightType = HighlightType.TextAdded)
        )
        val expectedMarkersForOldText = listOf(
            VimDiffRowMarker(fromColumn = 20, toColumn = 23, highlightType = HighlightType.TextRemoved),
            VimDiffRowMarker(fromColumn = 28, toColumn = 35, highlightType = HighlightType.TextRemoved)
        )
        LineDiffProcessor.getDiffMarkers(oldLine, newLine)
            .let { (markersForOldText, markersForNewText) ->
                expectedMarkersForNewText.verify(markersForNewText)
                expectedMarkersForOldText.verify(markersForOldText)
            }

    }

    private fun List<VimDiffRowMarker>.verify(actual: List<VimDiffRowMarker>) {
        assertEquals(this.size, actual.size)
        this.forEachIndexed { index, expectedMarker ->
            assertEquals(expectedMarker, actual[index], "Expected $expectedMarker at index $index but got ${ actual[index] }")
        }
    }
}
