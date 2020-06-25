package io.btc.utils

import java.time.Instant
import java.time.format.DateTimeFormatter

/** Keep Everything in UTC - always. Trust me, life is much simpler that way */
fun getCurrentTimeInIsoDateTime(): String {
    return DateTimeFormatter.ISO_INSTANT.format(Instant.now())
}
