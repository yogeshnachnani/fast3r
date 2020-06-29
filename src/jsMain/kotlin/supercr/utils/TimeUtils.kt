package supercr.utils

import codereview.FileLineItem
import kotlin.js.Date

fun String.iso8601ToHuman(): String {
    return Date(this).toLocaleString()
}
