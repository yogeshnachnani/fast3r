package supercr.css

import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Overflow
import kotlinx.css.Position
import kotlinx.css.background
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontStyle
import kotlinx.css.height
import kotlinx.css.maxHeight
import kotlinx.css.maxWidth
import kotlinx.css.minHeight
import kotlinx.css.overflow
import kotlinx.css.paddingBottom
import kotlinx.css.paddingLeft
import kotlinx.css.paddingRight
import kotlinx.css.paddingTop
import kotlinx.css.pc
import kotlinx.css.position
import kotlinx.css.px
import kotlinx.css.rgb
import kotlinx.css.vh
import kotlinx.css.width
import kotlinx.css.zIndex
import styled.StyleSheet

private fun String.cssClassName(): String {
    return "." + this
}

class TextStyles {
    companion object {
        const val insertedTextNew = "insertedText"
        const val textInsertedForBalance = "insertedTextOld"
        const val removedText = "removedText"
    }
}
enum class AvatarSize {
    tiny,
    small
}

object Colors {
    val baseText = rgb(61, 61, 61)
}

object FontSizes {
    val tiny = 11.px
    val small = 13.px
    val normal = 16.px
    val medium = 18.px
    val large = 21.px
    val extraLarge = 24.px
    val huge = 28.px
}
object FontFamilies {
    const val default = "roboto,sans-serif"
    const val nonCode = "Inter, Roboto, Sans-serif"
}

object ComponentStyles: StyleSheet("SuperCrCss", isStatic = true) {
    val fullHeight by css {
        height = 100.vh
    }
    val codeViewEditor by css {
        minHeight = 600.px
        maxHeight = 900.px
        fontSize = FontSizes.small
    }
    val fileItemText by css {
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.small
    }
    val tinyTextAvatar by css {
        height = FontSizes.normal
        width = 20.px
        fontSize = FontSizes.tiny
        fontFamily = FontFamilies.nonCode
    }
    val smallTextAvatar by css {
        height = FontSizes.medium
        width = 28.px
        fontSize = FontSizes.tiny
        fontFamily = FontFamilies.nonCode
    }
    val gutterLessListItem by css {
        paddingLeft = 0.px
        paddingRight = 0.px
    }
    val compactFileListItem by css {
        paddingLeft = 0.px
        paddingRight = 0.px
        paddingBottom = 0.px
        paddingTop = 0.px
        maxWidth = 190.px
    }
}

var styles = CSSBuilder().apply {
    TextStyles.insertedTextNew.cssClassName().invoke {
        background = "#51fc81"
        position = Position.absolute
        zIndex = 20
    }
    TextStyles.removedText.cssClassName().invoke {
        background = "#ffeef0"
        position = Position.absolute
        zIndex = 20
    }
    TextStyles.textInsertedForBalance.cssClassName().invoke {
        background = Color.lightBlue.value
        position = Position.absolute
        zIndex = 20
    }
    "body" {
        fontFamily = FontFamilies.default
        fontSize = FontSizes.normal
        overflow = Overflow.hidden
    }
}