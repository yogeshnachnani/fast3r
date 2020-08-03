package supercr.utils

import codereview.FileLineItem
import io.ktor.util.date.Month
import io.ktor.util.date.WeekDay
import kotlin.js.Date
import kotlin.js.json

fun String.iso8601ToHuman(): String {
    return Date(this).toLocaleString()
}

fun String.toDateTimeRepresentation(): String {
    val givenDate = Date(this)
    val dateRep = givenDate.toLocaleDateString(options = dateLocaleOptions {
        weekday = "long"
        month = "short"
        day = "numeric"
    })
    val timeRep = givenDate.toLocaleTimeString(options = dateLocaleOptions {
        hour12 = true
        hour = "2-digit"
        minute = "2-digit"
    })

    return "$dateRep | $timeRep"
}
