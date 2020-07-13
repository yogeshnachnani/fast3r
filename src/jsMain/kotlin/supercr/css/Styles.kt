package supercr.css

import kotlinx.css.BorderStyle
import kotlinx.css.BoxSizing
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Contain
import kotlinx.css.Display
import kotlinx.css.FontWeight
import kotlinx.css.Image
import kotlinx.css.LinearDimension
import kotlinx.css.Overflow
import kotlinx.css.Position
import kotlinx.css.StyledElement
import kotlinx.css.background
import kotlinx.css.backgroundColor
import kotlinx.css.backgroundImage
import kotlinx.css.backgroundSize
import kotlinx.css.borderColor
import kotlinx.css.borderRadius
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.boxShadow
import kotlinx.css.boxSizing
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontStyle
import kotlinx.css.fontWeight
import kotlinx.css.height
import kotlinx.css.hsl
import kotlinx.css.letterSpacing
import kotlinx.css.lineHeight
import kotlinx.css.margin
import kotlinx.css.marginBottom
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.maxHeight
import kotlinx.css.maxWidth
import kotlinx.css.minHeight
import kotlinx.css.overflow
import kotlinx.css.padding
import kotlinx.css.paddingBottom
import kotlinx.css.paddingLeft
import kotlinx.css.paddingRight
import kotlinx.css.paddingTop
import kotlinx.css.position
import kotlinx.css.properties.BoxShadows
import kotlinx.css.properties.LineHeight
import kotlinx.css.px
import kotlinx.css.vh
import kotlinx.css.width
import kotlinx.css.zIndex
import kotlinx.html.TEXTAREA
import styled.StyleSheet

private fun String.cssClassName(): String {
    return "." + this
}

class GutterDecorationStyles {
    companion object {
        const val commentIcon = "ace_gutter-comment-icon"
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

val commentBoxWidth = 190.px

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
    val genericListHeader by css {
        fontSize = FontSizes.large
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText1
        fontWeight = FontWeight.w700
    }
    val compactFileListItem by css {
        paddingLeft = 0.px
        paddingRight = 0.px
        paddingBottom = 0.px
        paddingTop = 0.px
        maxWidth = 190.px
        fontSize = FontSizes.small
    }

    val compactCommentListItem by css {
        paddingLeft = 0.px
        paddingRight = 0.px
        paddingBottom = 0.px
        paddingTop = 0.px
        maxWidth = 190.px
        fontSize = FontSizes.small
        display = Display.block
    }
    val commentInputBox by css {
        fontSize = FontSizes.small
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText4
        marginLeft = 5.px
        marginRight = 5.px
        marginBottom = 8.px
        maxWidth = 170.px
        width = 170.px
//        height = 32.px
        padding(vertical = 4.px, horizontal = 8.px)
//        put("resize", "none")
    }

    val reopLocalPathInputBox by css {
        fontSize = FontSizes.small
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText4
        marginLeft = 5.px
        marginRight = 5.px
        marginBottom = 8.px
        maxWidth = 150.px
        width = 150.px
//        height = 32.px
        padding(vertical = 4.px, horizontal = 8.px)
//        put("resize", "none")
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

    val loginComponentPaper by css {
        backgroundColor = Colors.background7
        fontSize = FontSizes.normal
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText1
        marginTop = 150.px
        padding(32.px)
    }
    val repoInitialiserRepoPathInput by css {
        ".MuiOutlinedInput-input" {
            fontSize = FontSizes.normal
            fontFamily = FontFamilies.code
            color = Colors.baseText1
        }
    }

    val commentPaper by css {
        backgroundColor = Colors.background7
        fontSize = FontSizes.normal
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText1
        maxWidth = commentBoxWidth
    }

    val actionBar by css {
        backgroundColor = Colors.background8
        fontSize = FontSizes.small
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText1
        width = LinearDimension.fillAvailable
        marginTop = 25.px
    }
    val actionBarItem by css {
        width = LinearDimension.fitContent
    }

    val diffViewNewText by css {
        backgroundColor = Colors.accentPrimary3
        position = Position.absolute
        zIndex = 20
    }
    val diffViewTextAddedForBalance by css {
        backgroundColor = Colors.accentPrimary1
        position = Position.absolute
        zIndex = 20
    }
    val diffViewDeletedText by css {
        background = "#ffeef0"
        position = Position.absolute
        zIndex = 20
    }
}

var styles = CSSBuilder().apply {
    "html" {
        fontFamily = FontFamilies.default
        fontSize = FontSizes.normal
        color = Colors.baseText1
        overflow = Overflow.hidden
        backgroundColor = Colors.background9
    }
    "body" {
        fontFamily = FontFamilies.default
        fontSize = FontSizes.normal
        color = Colors.baseText1
        overflow = Overflow.hidden
        backgroundColor = Colors.background9
    }
    ".ace-clouds-midnight .ace_gutter-active-line" {
        background = Colors.warmGrey1.value
    }
    ".ace-clouds-midnight .ace_marker-layer .ace_active-line" {
        background = Colors.warmGrey1.value
    }
    GutterDecorationStyles.commentIcon.cssClassName().invoke {
//        backgroundImage = Image("url(\"data:image/svg+xml,%3Csvg class='bi bi-x-circle' width='1em' height='1em' viewBox='0 0 16 16' fill='currentColor' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath fill-rule='evenodd' d='M8 15A7 7 0 1 0 8 1a7 7 0 0 0 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z'/%3E%3Cpath fill-rule='evenodd' d='M11.854 4.146a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708-.708l7-7a.5.5 0 0 1 .708 0z'/%3E%3Cpath fill-rule='evenodd' d='M4.146 4.146a.5.5 0 0 0 0 .708l7 7a.5.5 0 0 0 .708-.708l-7-7a.5.5 0 0 0-.708 0z'/%3E%3C/svg%3E\");")
        backgroundImage = Image("url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='${Colors.accentHighlight2}' %3E%3Cpath d='M20,2H4C2.897,2,2,2.897,2,4v18l5.333-4H20c1.103,0,2-0.897,2-2V4C22,2.897,21.103,2,20,2z M20,16H6.667L4,18V4h16V16z'/%3E%3C/svg%3E%0A\");")
        backgroundSize = "contain"
    }
}