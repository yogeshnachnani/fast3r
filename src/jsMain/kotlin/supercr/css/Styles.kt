package supercr.css

import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.LinearDimension
import kotlinx.css.Overflow
import kotlinx.css.Position
import kotlinx.css.background
import kotlinx.css.backgroundColor
import kotlinx.css.color
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.height
import kotlinx.css.hsl
import kotlinx.css.marginTop
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
    /** Backgrounds */
    val background9 = hsl(0, 0, 13)
    val background8 = hsl(0, 0, 26)
    val background7 = hsl(0, 0, 38)
    val background6 = hsl(0, 0, 46)
    val background5 = hsl(0, 0, 74)

    /** Text */
    val baseText4 = hsl(0, 0, 88)
    val baseText3 = hsl(0, 0, 93)
    val baseText = hsl(0, 0, 96)
    val baseText1 = hsl(0, 0, 98)

    val primary9 = hsl(235, 66, 30)
    val primary8 = hsl(233, 57, 37)
    val primary7 = hsl(232, 54, 41)
    val primary6 = hsl(232, 50, 45)
    val primaryBase = hsl(231, 48, 48)
    val primary4 = hsl(230, 44, 64)
    val primary3 = hsl(231, 44, 74)
    val primary2 = hsl(232, 45, 84)
    val primary1 = hsl(231, 44, 94)

    val accentPrimary9 =  hsl(173, 100, 21)
    val accentPrimary8 =  hsl(173, 100, 24)
    val accentPrimary7 =  hsl(174, 100, 27)
    val accentPrimary6 =  hsl(174, 100, 29)
    val accentPrimary5 =  hsl(174, 63, 40)
    val accentPrimary4 =  hsl(174, 42, 51)
    val accentPrimary3 =hsl(174, 42, 65)
    val accentPrimary2 = hsl(175, 41, 79)
    val accentPrimary1 = hsl(177, 41, 91)

    val accentHighlight1 = hsl(46, 100, 94)
    val accentHighlight2 = hsl(45, 100, 85)
    val accentHighlight3 = hsl(45, 100, 70)
    val accentHighlight4 = hsl(46, 100, 65)

    val warmGrey5 = hsl(41, 15, 28)
    val warmGrey4 = hsl(40, 12, 43)
    val warmGreyBase = hsl(39, 12, 58)
    val warmGrey2 = hsl(39, 16, 76)
    val warmGrey1 = hsl(39, 21, 88)
}

object FontSizes {
    val tiny = 11.px
    val small = 13.px
    val normal = 16.px
    val medium = 18.px
    val large = 20.px
    val extraLarge = 24.px
    val huge = 30.px
}
object FontFamilies {
    const val code = "Menlo, Consolas, 'DejaVu Sans Mono', monospace"
    const val default = "roboto,sans-serif"
    const val nonCode = "Inter, Roboto, Sans-serif"
}

object ComponentStyles: StyleSheet("SuperCrCss", isStatic = true) {
    val fullHeight by css {
        height = 100.vh
    }
    val maxWidthFitContent by css {
        maxWidth = LinearDimension.fitContent
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

    val backgroundAccentPrimary4 by css {
        backgroundColor = Colors.accentPrimary4
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
        fontSize = FontSizes.small
    }
    val fileListSectionSeparator by css {
        color = Colors.warmGrey2
    }

    val infoPaper by css {
        backgroundColor = Colors.background7
        fontSize = FontSizes.normal
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText1
    }

    val actionBar by css {
        backgroundColor = Colors.background8
        fontSize = FontSizes.small
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText1
        width = LinearDimension.fillAvailable
        marginTop = 10.px
    }
    val actionBarItem by css {
        width = LinearDimension.fitContent
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
    "html" {
        fontFamily = FontFamilies.default
        fontSize = FontSizes.normal
        overflow = Overflow.hidden
        backgroundColor = Colors.background9
    }
    "body" {
        fontFamily = FontFamilies.default
        fontSize = FontSizes.normal
        overflow = Overflow.hidden
        backgroundColor = Colors.background9
    }
    ".ace-clouds-midnight .ace_gutter-active-line" {
        background = Colors.warmGreyBase.value
    }
}