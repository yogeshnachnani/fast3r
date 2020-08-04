package supercr.utils

import kotlin.js.Date

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

fun String.ageInDaysFromNow(): Long  {
    val givenDate = Date(this)
    return (( Date().getTime() - givenDate.getTime() )/ (1000 * 3600 * 24)).toLong()
}

fun String.ageInHoursFromNow(): Long {
    val givenDate = Date(this)
    return (( Date().getTime() - givenDate.getTime() )/ (1000 * 3600 )).toLong()
}

fun String.ageInMinutesFromNow(): Long {
    val givenDate = Date(this)
    return (( Date().getTime() - givenDate.getTime() )/ ( 1000* 60 )).toLong()
}

fun String.getAgeFromNow(): String {
    val diffInDays = this.ageInDaysFromNow()
    return if ( diffInDays > 0) {
        "$diffInDays day${if (diffInDays > 1) "s" else ""}"
    } else {
        val diffInHours = this.ageInHoursFromNow()
        if (diffInHours < 1) {
            "${ageInMinutesFromNow()} mins"
        } else {
            "$diffInHours hour${if (diffInHours > 1) "s" else ""}"
        }
    }
}
