import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicTest {
    @Test
    fun thingsShouldWork() {
        assertEquals(listOf(1, 2, 3).reversed(), listOf(3, 2, 1))
    }

    @Test
    @Ignore
    fun thingsShouldBreak() {
        assertEquals(listOf(1, 2, 3).reversed(), listOf(1, 2, 3))
    }
}