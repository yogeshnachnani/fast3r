import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.withTimeout

/**
 * This nifty (read: hacky) utility is required to run tests that need to be run within coroutines
 * Since the main test body has to be part of a coroutine scope, there is no easy way to 'wait' for the test body to complete
 * This method helps us do that.
 * For more info:
 * (a) https://youtrack.jetbrains.com/issue/KT-22228
 * (b) https://blog.kotlin-academy.com/testing-common-modules-66b39d641617
 * (c) https://kotlinlang.slack.com/archives/C0B8L3U69/p1588176211439900?thread_ts=1588160258.436000&cid=C0B8L3U69
 */
fun <T> runTest(block: suspend () -> T): dynamic = GlobalScope.promise { block() }

