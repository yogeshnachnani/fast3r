import kotlinext.js.Object
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Scratchpad for quickly testing any external js imports
 */
class ExternalJsImportsTest  {
    @Test
    fun testDiffImport() {
        val oldLine = "System.out.println('This is 2nd line');"
        val newLine = "System.out.println('This is 2nd changed line');"
        val diff = diffWords(oldLine, newLine)
        assertEquals(2 , diff.filter { it.added == null && it.removed == null }.size)
        assertEquals(1, diff.filter { it.added == true }.size)
    }

}