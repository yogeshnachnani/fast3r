package io.btc.supercr.git.processor

import codereview.Edit
import codereview.SimpleFileDiff
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.junit.Test
import java.io.File
import java.nio.charset.Charset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiffProcessorTest {

    val rawTextOld = """
                public class FooBarBaz {
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd line");
                    System.out.println("This is 3rd line");
                    System.out.println("This is 4th line");
                  }
                }
            
        """.trimIndent()
    val rawTextNew = """
                public class FooBarBaz {
                  // Adding a comment - which will be a completely new line
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd changed line");
                    System.out.println("This is 3rd changed line");
                    System.out.println("This is a new line");
                    System.out.println("This is 4th line");

                }
        """.trimIndent()

    val editList = listOf(
        Edit(beginA = 1, endA = 1, beginB = 1, endB = 2),
        Edit(beginA = 3, endA = 5, beginB = 4, endB = 7),
        Edit(beginA = 6, endA = 8, beginB = 8, endB = 8),
        Edit(beginA = 9, endA = 9, beginB = 9, endB = 10)
    )

    @Test
    fun processEdits_ShouldReturnEmptyIfOldTextIsNulL() {
        val (oldFileLines, newFileLines) = listOf<Edit>().processWith(null, rawTextNew)
        assertTrue(oldFileLines.isEmpty())
        assertEquals(10, newFileLines.size)
    }

    @Test
    fun processEdits_ShouldReturnEmptyIfNewTextIsNulL() {
        val (oldFileLines, newFileLines) = listOf<Edit>().processWith(rawTextOld, null)
        assertTrue(newFileLines.isEmpty())
        assertEquals(9, oldFileLines.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun processEdits_ShouldBombIfBothTextIsEmpty() {
         listOf<Edit>().processWith(null, null)
    }

    @Test
    fun processEdits_ShouldAddLinesInOldTextToBalance() {
        val (oldFileLines, newFileLines) = editList.processWith(rawTextOld, rawTextNew)
        assertTrue(oldFileLines.size == newFileLines.size)
        assertEquals(12, oldFileLines.size)
        assertEquals(3 ,oldFileLines.filter { it.filePosition == null }.size)
    }

    @Test
    fun processEdits_ShouldAddLinesInNewTextToBalance() {
        val (oldFileLines, newFileLines) = editList.processWith(rawTextOld, rawTextNew)
        assertTrue(oldFileLines.size == newFileLines.size)
        assertEquals(12, newFileLines.size)
        assertEquals(2 ,newFileLines.filter { it.filePosition == null }.size)
    }

    @Test
    fun processEdits_ShouldAddLinesInNewTextWhenReplaceLengthBIsLessThanLengthA() {
        val simpleFileDiff = DiffProcessorTest::class.java.classLoader
            .getResource("textDiffWithEditList_ReplaceEditWithLengthBLesserThanA.json")!!
            .file
            .let {
                val json = Json(configuration = JsonConfiguration.Stable)
                json.parse(SimpleFileDiff.serializer(), FileUtils.readFileToString(File(it), Charset.defaultCharset()))
            }
        val (oldFileLines, newFileLines) = simpleFileDiff.editList.processWith(simpleFileDiff.oldFileText, simpleFileDiff.newFileText)
        assertEquals(254, oldFileLines.size)
        assertEquals(oldFileLines.size, newFileLines.size)
    }
}