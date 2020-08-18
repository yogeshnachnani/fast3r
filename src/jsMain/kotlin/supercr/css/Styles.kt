package supercr.css

import kotlinx.css.Align
import kotlinx.css.BackgroundRepeat
import kotlinx.css.BorderStyle
import kotlinx.css.BoxSizing
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.FlexWrap
import kotlinx.css.Float
import kotlinx.css.FontStyle
import kotlinx.css.FontWeight
import kotlinx.css.Image
import kotlinx.css.JustifyContent
import kotlinx.css.LinearDimension
import kotlinx.css.Outline
import kotlinx.css.Overflow
import kotlinx.css.Position
import kotlinx.css.TextAlign
import kotlinx.css.TextOverflow
import kotlinx.css.TextTransform
import kotlinx.css.VerticalAlign
import kotlinx.css.WhiteSpace
import kotlinx.css.alignContent
import kotlinx.css.alignItems
import kotlinx.css.background
import kotlinx.css.backgroundColor
import kotlinx.css.backgroundImage
import kotlinx.css.backgroundPosition
import kotlinx.css.backgroundRepeat
import kotlinx.css.backgroundSize
import kotlinx.css.basis
import kotlinx.css.borderBottomLeftRadius
import kotlinx.css.borderBottomRightRadius
import kotlinx.css.borderColor
import kotlinx.css.borderRadius
import kotlinx.css.borderRightColor
import kotlinx.css.borderRightStyle
import kotlinx.css.borderRightWidth
import kotlinx.css.borderStyle
import kotlinx.css.borderTopColor
import kotlinx.css.borderTopLeftRadius
import kotlinx.css.borderTopRightRadius
import kotlinx.css.borderTopStyle
import kotlinx.css.borderTopWidth
import kotlinx.css.borderWidth
import kotlinx.css.bottom
import kotlinx.css.boxSizing
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.em
import kotlinx.css.flex
import kotlinx.css.flexBasis
import kotlinx.css.flexDirection
import kotlinx.css.flexGrow
import kotlinx.css.flexWrap
import kotlinx.css.float
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontStyle
import kotlinx.css.fontWeight
import kotlinx.css.height
import kotlinx.css.hsl
import kotlinx.css.hsla
import kotlinx.css.justifyContent
import kotlinx.css.left
import kotlinx.css.letterSpacing
import kotlinx.css.lineHeight
import kotlinx.css.margin
import kotlinx.css.marginBottom
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.maxHeight
import kotlinx.css.maxWidth
import kotlinx.css.minWidth
import kotlinx.css.outline
import kotlinx.css.overflow
import kotlinx.css.padding
import kotlinx.css.paddingBottom
import kotlinx.css.paddingLeft
import kotlinx.css.paddingRight
import kotlinx.css.paddingTop
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.border
import kotlinx.css.properties.boxShadow
import kotlinx.css.properties.lh
import kotlinx.css.px
import kotlinx.css.rgba
import kotlinx.css.right
import kotlinx.css.textAlign
import kotlinx.css.textOverflow
import kotlinx.css.textTransform
import kotlinx.css.top
import kotlinx.css.verticalAlign
import kotlinx.css.vh
import kotlinx.css.vw
import kotlinx.css.whiteSpace
import kotlinx.css.width
import kotlinx.css.zIndex
import styled.StyleSheet

private fun String.cssClassName(): String {
    return "." + this
}

class GutterDecorationStyles {
    companion object {
        const val commentIcon = "ace_gutter-comment-icon"
    }
}

object Colors {
    val accentHighlight2 = hsl(45, 100, 85)

    val warmGrey5 = hsl(41, 15, 28)
    val warmGrey4 = hsl(40, 12, 43)
    val warmGreyBase = hsl(39, 12, 58)
    val warmGrey2 = hsl(39, 16, 76)
    val warmGrey1 = hsl(39, 21, 88)

    val primaryBlack = hsl(224, 22, 10)
    val primaryBlue = hsl(234, 81, 57)
    val primaryTeal = hsl(169, 92, 71)

    val backgroundDarkestGrey = hsl(222, 20, 17)
    val backgroundDarkestGreyAlpha04 = hsla(222, 20, 17, 0.4)
    val backgroundDarkGrey = hsl(224, 19, 23)
    val backgroundMediumGrey = hsl(220, 22, 24)
    val backgroundGrey = hsl(220, 21, 27)

    val highlightsGreyAlphaPoint1 = hsla(0, 2, 77, 0.1)

    val iconGrey = hsl(205, 15, 55)
    val lineSeparatorBackground = hsla(205, 15, 55, 0.2)
    val textDarkGrey = hsl(204, 16, 50)
    val textMediumGrey = hsl(204, 26, 77)
    val textLightGrey = hsl(0, 0, 93)

}

object EditorThemeColors {
//    val tokenComment = Colors.textDarkGrey
    val gutterBackground = Colors.backgroundDarkestGrey.withAlpha(0.6)
    val gutterColor = hsla(0, 0, 93, 0.5)
    val editorBackground = Colors.primaryBlack
    val editorColor = Colors.textMediumGrey
    val gutterActiveLineBorderColor = Colors.primaryTeal
    val lightGreen = hsl(165, 100, 69)
    val highlightsLightGreen = hsla(159, 42, 42, 0.3)
    val highlightsGreen = hsla(159, 76, 43, 0.7)
    val highlightsLightRead = hsla(0, 48, 45, 0.2)
    val gutterHighlightsLightRed = hsla(0, 48, 45, 0.3)
    val highlightsRed = hsla(0, 61, 55, 0.7)
    val highlightsGrey = hsla(0, 2, 77, 0.22)

    val tokenOrange = hsl(29, 100, 58)
    val tokenLightBlue = hsl(198, 100, 70)
    val tokenBlue = hsl(234, 100, 69)
    val tokenPink = hsl(0, 100, 74)
    val tokenLightGreen = hsl(165, 100, 69)
    val tokenGreen = hsl(158, 86, 35)
    val tokenYellow = hsl(54, 100, 50)
    val tokenLightOrange = hsl(40, 98, 61)
    val tokenRed = hsl(0, 100, 66)
    val tokenLightPurple = hsl(328, 100, 73)
    val tokenPurple = hsl(289, 100, 69)
}

object FontSizes {
    val tiny = 12.px
    val small = 13.px
    val normal = 16.px
    val medium = 18.px
    val large = 20.px
    val extraLarge = 24.px
    val crayCray = 38.px
}
object LineHeights {
    val tiny = 20.px.lh
    val normal = 22.px.lh
    val extraLarge = 32.px.lh
    val large = 28.px.lh
    val crayCray = 48.px.lh
}

object FontFamilies {
    const val code = "Menlo, Consolas, 'DejaVu Sans Mono', monospace"
    const val nonCode = "Inter, Roboto, Sans-serif"
}

val commentBoxWidth = 312.px
val loginScreenItemsLeftMargin = 36.vw

const val MACBOOK_13inch = "screen and (max-width: 1449px) and (min-width: 950px)"
const val MACBOOK_15inch = "screen and (max-width: 2040px) and (min-width: 1450px)"
const val QUAD_HD = "screen and (max-width: 2560px) and (min-width: 2041px)"
const val FOUR_K = "screen and (min-width: 2561px)"

object ComponentStyles: StyleSheet("SuperCrCss", isStatic = true) {
    val headline1 by css {
        fontSize = 93.px
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.w300
        letterSpacing = (-1.5).px
    }
    val headline2 by css {
        fontSize = 58.px
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.w300
        letterSpacing = (-0.5).px
    }
    val headline3 by css {
        fontSize = 47.px
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.normal
        letterSpacing = 0.px
    }
    val headline4 by css {
        fontSize = 30.px
        lineHeight = 40.px.lh
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.normal
        letterSpacing = (0.25).px
    }
    val headline5 by css {
        fontSize = 24.px
        lineHeight = 32.px.lh
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.normal
        letterSpacing = 0.px
    }
    val headline6 by css {
        fontSize = 19.px
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.w600
        letterSpacing = (0.15).px
    }

    val bodyFont by css {
        fontSize = 21.px
        lineHeight = 28.px.lh
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.normal
        media(QUAD_HD) {
            fontSize = 24.px
            lineHeight = 32.px.lh
        }
        media(FOUR_K) {
            fontSize = 24.px
            lineHeight = 32.px.lh
        }
    }

    val caption by css {
        fontSize = 16.px
        lineHeight = 22.px.lh
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.normal
    }
    val helpTextCaption by css {
        fontSize = 12.px
        lineHeight = 16.px.lh
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.normal
        media(QUAD_HD) {
            fontSize = 16.px
            lineHeight = 22.px.lh
        }
        media(FOUR_K) {
            fontSize = 16.px
            lineHeight = 22.px.lh
        }
    }

    val body2Font by css {
        fontSize = 18.px
        lineHeight = 24.px.lh
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.normal
        media(QUAD_HD) {
            fontSize = 21.px
            lineHeight = 28.px.lh
        }
        media(FOUR_K) {
            fontSize = 21.px
            lineHeight = 28.px.lh
        }
    }

    val buttonCaption by css {
        fontSize = 16.px
        lineHeight = 22.px.lh
        fontFamily = FontFamilies.nonCode
        fontWeight = FontWeight.normal
        color = Colors.textDarkGrey
    }

    val primaryActionButtonText by css {
        + bodyFont
        fontWeight = FontWeight.w600
        backgroundColor = Colors.primaryBlue
        color = Colors.textLightGrey
    }

    val codeFont by css {
        fontSize = 13.px
        lineHeight = 17.px.lh
        media(QUAD_HD) {
            fontSize = 16.px
            lineHeight = 22.px.lh
        }
        media(FOUR_K) {
            fontSize = 16.px
            lineHeight = 22.px.lh
        }
    }

    val keyboardShortcutSingleCharBox by css {
        backgroundColor = Colors.backgroundDarkestGrey
        borderRadius = 4.px
        fontStyle = FontStyle.normal
        width = 28.px
        height = 28.px
        + headline6
        fontStyle = FontStyle.normal
        media(QUAD_HD) {
            width = 42.px
            height = 42.px
            + headline4
        }
        media(FOUR_K) {
            width = 42.px
            height = 42.px
            + headline4
        }
        display = Display.flex
        textAlign = TextAlign.center
        justifyContent = JustifyContent.center
        alignItems = Align.center
        alignContent = Align.center
        color = Colors.textMediumGrey
    }

    val fileListPane by css {
        paddingTop = 0.px
        minWidth = 340.px
        maxWidth = 360.px
        media(QUAD_HD) {
            paddingTop = 30.px
            minWidth = 450.px
        }
        media(FOUR_K) {
            paddingTop = 30.px
            minWidth = 450.px
        }
        height = 100.vh
        paddingRight = 8.px
        margin(all = 0.px)
        backgroundColor = Colors.backgroundDarkGrey
    }
    val fileListHeaderItem by css {
        + headline6
        fontWeight = FontWeight.normal
        margin(vertical = 16.px)
        media(QUAD_HD) {
            margin(vertical = 40.px)
        }
        media(FOUR_K) {
            margin(vertical = 40.px)
        }
        color = Colors.textDarkGrey
        padding(0.px)
        maxWidth = 480.px
        maxHeight = 60.px
    }
    val fileListItem by css {
        padding(all = 0.px)
        maxWidth = 360.px
        media(QUAD_HD) {
            maxWidth = 480.px
        }
        media(FOUR_K) {
            maxWidth = 480.px
        }
        maxHeight = 60.px
        + body2Font
    }

    val fileListItemContainer by css {
        margin(all = 0.px)
        padding(vertical = 8.px)
        media(QUAD_HD) {
            padding(vertical = 18.px)
        }
        media(FOUR_K) {
            padding(vertical = 18.px)
        }
        alignContent = Align.baseline
        + body2Font
    }

    val fileList by css {
        maxWidth = 360.px
        media(QUAD_HD) {
            maxWidth = 480.px
        }
        media(FOUR_K) {
            maxWidth = 480.px
        }
        maxHeight = 80.vh
        width = 100.pct
        overflow = Overflow.auto
    }

    val fileListTshirtSizePosition by css {
        float = Float.left
        minWidth = 10.pct
        marginRight = 4.px
        marginLeft = 24.px
        media(QUAD_HD) {
            marginLeft = 40.px
        }
        media(FOUR_K) {
            marginLeft = 40.px
        }
    }

    val fileListItemKeyboardShortcutContainer by css {
        marginRight = 24.px
        media(QUAD_HD) {
            marginRight = 36.px
        }
        media(FOUR_K) {
            marginRight = 36.px
        }
    }

    val fileListHeaderIcon by css {
        float = Float.left
        minWidth = 10.pct
        marginRight = 4.px
        marginLeft = 32.px
        color = Colors.iconGrey
    }

    val fileListHeaderText by css {
        + bodyFont
        fontWeight = FontWeight.w600
        margin(vertical = 8.px)
    }

    val fileListExpandIcon by css {
        display = Display.inlineFlex
        position = Position.absolute
        right = 4.px
        top = 16.px
//        marginLeft = 32.px
        color = Colors.iconGrey
    }

    val selectedFileListItem by css {
        backgroundColor = Colors.highlightsGreyAlphaPoint1
    }

    val commentThreadContainer by css {
        width = 100.pct
        marginBottom = 0.px
        marginTop = 20.px
    }

    val commentThreadDateTime by css {
        fontSize = FontSizes.tiny
        lineHeight = LineHeights.tiny
        marginTop = 4.px
        marginLeft = 20.px
        marginBottom = 0.px
        color = Colors.textDarkGrey
    }

    val commentThreadCommentBody by css {
        marginTop = 12.px
        fontSize = FontSizes.tiny
        lineHeight = LineHeights.tiny
        marginLeft = 20.px
        marginRight = 24.px
        marginBottom = 20.px
        color = Colors.textMediumGrey
    }

    val commentThreadSeparator by css {
        marginTop = 0.px
        marginBottom = 0.px
        marginLeft = 22.px
        marginRight = 32.px
        height = 1.px
        backgroundColor = Colors.lineSeparatorBackground
    }

    val commentThreadUserId by css {
        fontSize = FontSizes.normal
        lineHeight = LineHeights.normal
        fontWeight = FontWeight.w600
        color = Colors.textDarkGrey
        marginTop = 0.px
        marginLeft = 20.px
        marginRight = 0.px
        marginBottom = 0.px
    }
    val compactCommentListItem by css {
        paddingLeft = 0.px
        paddingRight = 0.px
        paddingBottom = 0.px
        paddingTop = 0.px
        maxWidth = commentBoxWidth
        fontSize = FontSizes.small
        flexWrap = FlexWrap.wrap
    }
    val compactCommentListItemInputBox by css {
        paddingBottom = 14.px
    }

    val commentPaper by css {
        backgroundColor = Colors.backgroundMediumGrey
        borderTopStyle = BorderStyle.solid
        borderTopWidth = 6.px
        borderTopColor = Colors.primaryBlue
        borderTopLeftRadius = 8.px
        borderTopRightRadius = 8.px
        borderRadius = 8.px
        boxShadow(color = rgba(0, 0, 0, 0.25), offsetX = 0.px, offsetY = 4.px, blurRadius = 34.px)
        fontSize = FontSizes.normal
        fontFamily = FontFamilies.nonCode
        color = Colors.textMediumGrey
        maxWidth = commentBoxWidth
    }

    val commentInputBox by css {
        fontSize = FontSizes.tiny
        lineHeight = LineHeights.tiny
        fontFamily = FontFamilies.nonCode
        color = Colors.textMediumGrey
        marginLeft = 20.px
        marginRight = 20.px
//        marginBottom = 14.px
        width = 100.pct
        borderRadius = 4.px
        backgroundColor = Colors.backgroundDarkestGrey
        borderStyle = BorderStyle.solid
        borderWidth = 1.px
        borderColor = Colors.backgroundGrey
//        height = 32.px
        padding(vertical = 12.px, horizontal = 12.px)
//        put("resize", "none")
    }
    val emptyThreadCommentInputBox by css {
        marginTop = 20.px
    }

    val ctrlEnterSendHelpMessage by css {
        marginTop = 8.px
        marginBottom = 0.px
        marginRight = 20.px
        display = Display.flex
        alignItems = Align.flexEnd
        justifyContent = JustifyContent.flexEnd
        color = Colors.textDarkGrey
        lineHeight = LineHeights.tiny
        fontSize = FontSizes.tiny
        fontFamily = FontFamilies.nonCode
        flex(flexBasis = 100.pct.basis)
    }

    val fileViewPane by css {
        display = Display.flex
        flexWrap = FlexWrap.wrap
        boxSizing = BoxSizing.borderBox
        width = 100.pct
        alignContent = Align.flexStart
        alignItems = Align.flexStart
    }

    val fileViewFileInfo by css {
        backgroundColor = Colors.backgroundDarkestGrey
        + body2Font
        width = 100.pct
        display = Display.flex
        alignItems = Align.flexEnd
        height = 34.px
        media(QUAD_HD) {
            height = 40.px
        }
        media(FOUR_K) {
            height = 40.px
        }
    }
    val fileViewFileInfoText by css {
//        marginTop = 24.px
//        marginBottom = 24.px
        marginLeft = 36.px
        color = Colors.textDarkGrey
    }

    val loginScreen by css {
        width = 100.vw
        height = 100.vh
        backgroundImage = Image("url(\"login_screen_background.png\")")
        backgroundPosition = "center"
        backgroundRepeat = BackgroundRepeat.noRepeat
        backgroundSize = "cover"
        flexWrap = FlexWrap.wrap
//        display = Display.flex
//        transform {
//            rotate(( -10.81 ).deg)
//        }
//        opacity = 0.6
//        display = Display.inlineBlock
    }
    val loginScreenMessage by css {
//        marginLeft = loginScreenItemsLeftMargin
//        marginTop = 40.vh
        color = Colors.textLightGrey
        display = Display.flex
        + headline4
    }

    val loginScreenOrMessage by css {
        width = 100.pct
        marginTop = 50.px
        color = Colors.textDarkGrey
        + headline5
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
    }

    val loginScreenOrLine by css {
        flexBasis = 48.pct.basis
        height = 1.px
        backgroundColor = Colors.backgroundGrey
    }

    val loginScreenOrText by css {
        flexBasis = 4.pct.basis
        textAlign = TextAlign.center
    }

    val loginScreenDemoCredentialsContainer by css {
        width = 100.pct
        marginTop = 20.px
        color = Colors.textLightGrey
        textAlign = TextAlign.center
        display = Display.inlineFlex
        justifyContent = JustifyContent.center
        + headline4
    }

    val loginScreenDemoButton by css {
        backgroundColor = Color.transparent
        border(2.px, BorderStyle.solid, Colors.primaryBlue, 8.px)
        + headline5
        fontWeight = FontWeight.w600
        color = Colors.textMediumGrey
        textTransform = TextTransform.none
        width = 100.pct
        justifyContent = JustifyContent.spaceBetween
        padding(vertical = 24.px, horizontal = 38.px)
    }
    val loginScreenPressCtrlEnterLabel by css {
        width = LinearDimension.fitContent
        + buttonCaption
        marginLeft = 30.px
        display = Display.inlineBlock
    }

    val loginScreenUsernameBoxContainer by css {
//        marginLeft = loginScreenItemsLeftMargin
        marginTop = 44.px
        backgroundColor = Colors.backgroundDarkGrey
        border(1.px, BorderStyle.solid, Colors.backgroundGrey, 11.px)
        boxSizing = BoxSizing.borderBox
        width = 100.pct
        height = 100.px
        display = Display.inlineFlex
//        alignContent = Align.flexEnd
    }

    val loginScreenUserIcon by css {
        float = Float.left
        width = 44.px
        height = 44.px
        marginLeft = 30.px
        marginTop = 28.px
        marginBottom = 28.px
        color = Colors.iconGrey
        fontSize = 44.px
    }

    val loginGithubUsername by css {
//        maxWidth = 445.px
        height = 48.px
        fontSize = FontSizes.crayCray
        lineHeight = LineHeights.crayCray
        color = Colors.textMediumGrey
        backgroundColor = Colors.backgroundDarkGrey
        marginTop = 28.px
        marginLeft = 32.px
        borderStyle = BorderStyle.none
        padding(vertical = 0.px, horizontal = 2.px)
        outline = Outline.none
    }

    val loginPressEnterLabel by css {
        width = LinearDimension.fitContent
        + buttonCaption
        marginTop = 38.px
        marginBottom = 38.px
        marginRight = 24.px
        display = Display.inlineFlex
        flexWrap = FlexWrap.nowrap
        whiteSpace = WhiteSpace.nowrap
    }
    val loginGo by css {
        borderTopRightRadius = 11.px
        borderBottomRightRadius = 11.px
        borderTopLeftRadius = 0.px
        borderBottomLeftRadius = 0.px
        + primaryActionButtonText
        padding(vertical = 34.px, horizontal = 38.px)
    }

    val repoInitialiserRepoPathInput by css {
        ".MuiOutlinedInput-input" {
            + bodyFont
            fontWeight = FontWeight.w600
            color = Colors.textDarkGrey.lighten(20)
            backgroundColor = Colors.backgroundDarkestGrey
            border(width = 1.px, style = BorderStyle.solid, color = Colors.backgroundGrey, borderRadius = 6.px)
            paddingLeft = 32.px
        }
    }

    val extraInfoWindowContainer by css {
        backgroundColor = Colors.backgroundDarkestGrey
        width = 100.pct
        height = 100.pct
        marginTop = 0.px
        borderTopStyle = BorderStyle.solid
        borderTopWidth = 4.px
        borderTopColor = Colors.backgroundDarkestGrey
    }

    val extraInfoWindowHeader by css {
        height = 36.px
        media(QUAD_HD) {
            height = 60.px
        }
        media(FOUR_K) {
            height = 60.px
        }
        backgroundColor = Colors.primaryBlack
        width = 100.pct
    }

    val actionBarShortcutContainer by css {
        display = Display.flex
        flexBasis = 22.pct.basis
        justifyContent = JustifyContent.spaceBetween
        alignItems = Align.center
        alignContent = Align.center
    }

    val actionBar by css {
        position = Position.absolute
        right = 2.px
        bottom = 20.vh
        backgroundColor = Colors.backgroundDarkestGrey
        borderBottomLeftRadius = 14.px
        borderTopLeftRadius = 14.px
        borderTopRightRadius = 0.px
        borderBottomRightRadius = 0.px
        + helpTextCaption
        color = Colors.textDarkGrey
        height = 48.px
        width = 580.px
        media(QUAD_HD) {
            height = 80.px
            width = 900.px
        }
        media(FOUR_K) {
            height = 80.px
            width = 900.px
        }
        display = Display.flex
        justifyContent = JustifyContent.spaceAround
        alignContent = Align.center
    }

    val actionBarKeyboardLetterBox by css {
        backgroundColor = Colors.backgroundGrey
        borderRadius = 4.px
        width = 24.px
        height = 24.px
        + caption
        fontStyle = FontStyle.normal
        media(QUAD_HD) {
            width = 42.px
            height = 42.px
            marginTop = 20.px
            marginBottom = 20.px
            + headline4
        }
        media(FOUR_K) {
            width = 42.px
            height = 42.px
            marginTop = 20.px
            marginBottom = 20.px
            + headline4
        }
        display = Display.flex
        textAlign = TextAlign.center
        justifyContent = JustifyContent.center
        alignItems = Align.center
        alignContent = Align.center
        color = Colors.textMediumGrey
    }

    val diffViewNewText by css {
        backgroundColor = EditorThemeColors.highlightsGreen
        position = Position.absolute
        zIndex = 20
    }
    val diffViewNewTextBackground by css {
        backgroundColor = EditorThemeColors.highlightsLightGreen
        position = Position.absolute
    }
    val diffViewTextAddedForBalanceBackground by css {
        backgroundColor = EditorThemeColors.highlightsGrey
        position = Position.absolute
    }
    val diffViewDeletedText by css {
        backgroundColor = EditorThemeColors.highlightsRed
        position = Position.absolute
        zIndex = 20
    }
    val diffViewDeletedTextBackground by css {
        backgroundColor = EditorThemeColors.highlightsLightRead
        position = Position.absolute
    }
    val diffViewDeletedTextGutter by css {
        backgroundColor = EditorThemeColors.gutterHighlightsLightRed
        position = Position.absolute
    }

    val overviewScreenNumPullRequests by css {
        color = Colors.textDarkGrey
        marginBottom = 70.px
        + headline4
    }

    val pullRequestSummaryCard by css {
        width = 100.pct
        backgroundColor = Colors.backgroundDarkGrey
        borderRadius = 8.px
        boxShadow(color = rgba(0, 0, 0, 0.25), offsetX = 0.px, offsetY = 4.px, blurRadius = 74.px )
        margin(0.px)
        padding(0.px)
    }

    val pullRequestSummaryCardHeading by css {
        marginTop = 12.px
        marginLeft = 24.px
        marginBottom = 0.px
        + bodyFont
        width = LinearDimension.fillAvailable
        color = Colors.textMediumGrey
        media(QUAD_HD) {
            marginTop = 24.px
        }
        media(FOUR_K) {
            marginTop = 24.px
        }
    }
    val pullRequestSummaryHeaderContainer by css {
        display = Display.inlineFlex
        justifyContent = JustifyContent.spaceBetween
        width = 100.pct
        margin(0.px)
    }

    val pullRequestSummaryAgeRibbon by css {
        borderTopLeftRadius = 100.px
        borderBottomLeftRadius = 100.px
        height = 36.px
        + buttonCaption
        color = Colors.textLightGrey
        verticalAlign = VerticalAlign.middle
        textAlign = TextAlign.center
        marginTop = 12.px
        whiteSpace = WhiteSpace.nowrap
        media(QUAD_HD) {
            marginTop = 24.px
            marginLeft = 24.px
        }
        media(FOUR_K) {
            marginTop = 24.px
            marginLeft = 24.px
        }
    }

    val pullRequestSummaryAgeText by css {
        marginLeft = 20.px
        marginRight = 36.px
        marginTop = 6.px
        marginBottom = 6.px
        display = Display.inlineBlock
    }

    val pullRequestSummaryProjectName by css {
        backgroundColor = Colors.backgroundDarkestGreyAlpha04
        borderRadius = 100.px
        width = LinearDimension.fitContent
        + caption
        color = Colors.textLightGrey
        marginTop = 8.px
        marginBottom = 8.px
        marginLeft = 24.px
        padding(vertical = 4.px, horizontal = 8.px)
        media(QUAD_HD) {
            marginTop = 12.px
            marginBottom = 12.px
            padding(vertical = 6.px, horizontal = 12.px)
        }
        media(FOUR_K) {
            marginTop = 12.px
            marginBottom = 12.px
            padding(vertical = 6.px, horizontal = 12.px)
        }
        display = Display.block
    }

    val pullRequestSummaryCommentContainer by css {
        borderRadius = 5.px
        backgroundColor = Colors.backgroundDarkestGrey
        marginLeft = 24.px
        marginRight = 24.px
        display = Display.flex
        justifyContent = JustifyContent.spaceAround
        color = Colors.textDarkGrey
        + caption
        position = Position.relative
        marginBottom = 8.px
        media(QUAD_HD) {
            marginBottom = 16.px
        }
        media(FOUR_K) {
            marginBottom = 16.px
        }
    }

    val pullRequestSummaryCommentBody by css {
        height = LinearDimension(LineHeights.normal.value).times(2)
        textOverflow = TextOverflow.ellipsis
        overflow = Overflow.hidden
        put("display", "-webkit-box")
        put("-webkit-line-clamp", "2")
        put("-webkit-box-orient", "vertical")
        marginBottom = 16.px
        media(QUAD_HD) {
            marginBottom = 22.px
        }
        media(FOUR_K) {
            marginBottom = 22.px
        }
    }

    val pullRequestSummaryCommentUserAvatar by css {
//        position = Position.absolute
        marginTop = 1.em
        display = Display.flex
//        top = 24.px
//        left = 24.px
    }

    val pullRequestMetaDataContainer by css {
        marginLeft = 24.px
        marginRight = 24.px
        display = Display.flex
        justifyContent = JustifyContent.spaceBetween
        alignContent = Align.center
        paddingBottom = 14.px
    }

    val pullRequestMetaDataItems by css {
        width = LinearDimension.fitContent
        display = Display.inlineFlex
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
        whiteSpace = WhiteSpace.nowrap
    }
    val pullRequestSummaryMetaDataText by css {
        display = Display.inlineFlex
        color = Colors.textMediumGrey
        marginLeft = 8.px
    }
    val pullRequestSummaryMetaDataEstTime by css {
        display = Display.inlineFlex
        fontSize = FontSizes.normal
        color = Colors.primaryBlue
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
    }

    val pullRequestSummaryMetaDataSize by css {
        display = Display.inlineFlex
        + caption
        color = Colors.primaryBlue
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
        marginLeft = 28.px
        media(QUAD_HD) {
            marginLeft = 42.px
        }
        media(FOUR_K) {
            marginLeft = 42.px
        }
    }

    val pullRequestSummaryCardKeyboardShortcut by css {
        backgroundColor = Colors.backgroundDarkestGrey
        borderRadius = 4.px
        width = 28.px
        height = 28.px
        + headline6
        fontStyle = FontStyle.normal
        media(QUAD_HD) {
            width = 42.px
            height = 42.px
            + headline4
        }
        media(FOUR_K) {
            width = 42.px
            height = 42.px
            + headline4
        }
        display = Display.flex
        textAlign = TextAlign.center
        justifyContent = JustifyContent.center
        alignItems = Align.center
        alignContent = Align.center
        color = Colors.textMediumGrey
    }

    val avatarOrangeBackground by css {
        backgroundColor = EditorThemeColors.tokenOrange
    }
    val avatarPurpleBackground by css {
        backgroundColor = EditorThemeColors.tokenPurple
    }

    val avatarInitials by css {
        width = 28.px
        height = 28.px
        + caption
        media(QUAD_HD) {
            width = 34.px
            height = 34.px
        }
        media(FOUR_K) {
            width = 34.px
            height = 34.px
        }
    }

    val changeSetOverviewTitleAndAge by css {
        display = Display.flex
        justifyContent = JustifyContent.flexStart
        fontSize = FontSizes.extraLarge
        lineHeight = LineHeights.extraLarge
        color = Colors.textMediumGrey
        marginBottom = 18.px
        width = 65.pct
    }

    val changeSetOverviewPullRequestAgeRibbonBalancer by css {
        display = Display.inlineFlex
        marginTop = 8.px
        media(QUAD_HD) {
            marginTop = 0.px
        }
        media(FOUR_K) {
            marginTop = 0.px
        }
    }

    val changeSetOverviewMetaInfo by css {
        display = Display.flex
        justifyContent = JustifyContent.spaceEvenly
        + caption
        color = Colors.textMediumGrey
        whiteSpace = WhiteSpace.nowrap
        flex(flexBasis = 40.pct.basis)
        width = 45.pct
        marginBottom = 30.px
        media(QUAD_HD) {
            width = 25.pct
            marginBottom = 100.px
        }
        media(FOUR_K) {
            width = 25.pct
            marginBottom = 100.px
        }
    }

    val changeSetOverviewMetaInfoItems by css {
        width = LinearDimension.fitContent
        display = Display.inlineFlex
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
        flex(flexBasis = 70.pct.basis)
    }
    val changeSetOverviewMetaInfoProjectName by css {
        backgroundColor = Colors.textMediumGrey.withAlpha(0.4)
        borderRadius = 100.px
        width = LinearDimension.fitContent
        fontSize = FontSizes.normal
        lineHeight = LineHeights.normal
        fontFamily = FontFamilies.nonCode
        color = Colors.textLightGrey
        marginLeft = 0.px
        marginTop = 12.px
        marginBottom = 12.px
        padding(vertical = 6.px, horizontal = 12.px)
    }
    val changeSetOverviewMetaInfoEstTime by css {
        display = Display.inlineFlex
        fontSize = FontSizes.normal
        color = Colors.primaryBlue
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
        flexGrow = 1.0
    }
    val reviewCommentsContainer by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        /** Keep this similar to [changeSetOverviewFileList] */
        maxHeight = ((40 + 14) * 12).px
        marginBottom = 30.px
        media(QUAD_HD) {
            maxHeight = ((60 + 24) * 15).px
            marginBottom = 70.px
        }
        media(FOUR_K) {
            maxHeight = ((60 + 24) * 15).px
            marginBottom = 70.px
        }
        overflow = Overflow.auto
        "::-webkit-scrollbar-thumb" {
            borderRadius = 6.px
            backgroundColor = Colors.backgroundMediumGrey
        }
        "::-webkit-scrollbar" {
            width = 10.px
            backgroundColor = Colors.backgroundDarkestGrey
        }
    }

    val reviewCommentContainer by css {
        display = Display.flex
        justifyContent = JustifyContent.flexStart
        position = Position.relative
        marginBottom = 14.px
        media(QUAD_HD) {
            marginBottom = 24.px
        }
        media(FOUR_K) {
            marginBottom = 24.px
        }
    }
    val reviewCommentAvatarContainer by css {
        display = Display.inlineFlex
        flexBasis = 5.pct.basis
    }

    val reviewCommentBox by css {
        display = Display.block
        flex(flexBasis = 88.pct.basis)
        backgroundColor = Colors.backgroundDarkestGrey
        borderRadius = 8.px
        padding(vertical = 8.px, horizontal = 16.px)
        media(QUAD_HD) {
            padding(vertical = 20.px, horizontal = 30.px)
        }
        media(FOUR_K) {
            padding(vertical = 20.px, horizontal = 30.px)
        }
        + caption
        color = Colors.textMediumGrey
    }

    val changeSetReviewButton by css {
        backgroundColor = Colors.primaryBlue
        borderRadius = 6.px
        fontWeight = FontWeight.w600
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.extraLarge
        lineHeight = LineHeights.extraLarge
        color = Colors.textLightGrey
        padding(vertical = 14.px, horizontal = 26.px)
        width = 135.px
        textTransform = TextTransform.none
    }

    val changeSetOverviewReviewButtonContainer by css {
        /** That's how bad I am at this :facepalm: */
        width = 99.pct
        media(QUAD_HD) {
            width = 95.pct
        }
        media(FOUR_K) {
            width = 95.pct
        }
    }

    val changeSetOverviewPressEnterTextContainer by css {
        display = Display.inlineFlex
        justifyContent = JustifyContent.flexEnd
    }
    val changeSetOverviewPressEnterText by css {
        width = LinearDimension.fitContent
        display = Display.inlineFlex
        whiteSpace = WhiteSpace.nowrap
    }

    val changeSetOverviewFileItemContainer by css {
        backgroundColor = Colors.backgroundDarkGrey
        borderRadius = 8.px
        + body2Font
        color = Colors.textMediumGrey
        justifyContent = JustifyContent.spaceAround
        display = Display.inlineFlex
        width = 80.pct
        marginBottom = 14.px
        height = 40.px
        media(QUAD_HD) {
            marginBottom = 24.px
            height = 60.px
        }
        media(FOUR_K) {
            marginBottom = 24.px
            height = 60.px
        }
    }

    val compactList by css {
        padding(all = 0.px)
    }
    val changeSetOverviewFileList by css {
        /** size it roughly equal to 13 items */
        maxHeight = ((40 + 14) * 13).px
        media(QUAD_HD) {
            /** size it roughly equal to 15 items */
            maxHeight = ((60 + 24) * 15).px
        }
        media(FOUR_K) {
            /** size it roughly equal to 15 items */
            maxHeight = ((60 + 24) * 15).px
        }
        overflow = Overflow.auto
        "::-webkit-scrollbar-thumb" {
            borderRadius = 6.px
            backgroundColor = Colors.backgroundMediumGrey
        }
        "::-webkit-scrollbar" {
            width = 10.px
            backgroundColor = Colors.backgroundDarkestGrey
        }
    }

    val changeSetOverviewFileListTitle by css {
        display = Display.flex
        justifyContent = JustifyContent.spaceAround
        + headline5
        color = Colors.textMediumGrey
        marginBottom = 18.px
        flexWrap = FlexWrap.wrap
    }

    val changeSetOverViewFileListSubText by css {
        marginTop = 14.px
        media(QUAD_HD) {
            marginTop = 28.px
        }
        media(FOUR_K) {
            marginTop = 28.px
        }
        + buttonCaption
        flexBasis = 100.pct.basis
    }

    val repoMappingTitle2 by css {
        fontSize = FontSizes.small
        lineHeight = LineHeights.tiny
        fontFamily = FontFamilies.nonCode
        color = Colors.textMediumGrey
        marginBottom = 20.px
    }

    val repoMappingTitle by css {
        marginBottom = 20.px
        media(QUAD_HD) {
            marginBottom = 30.px
        }
        media(FOUR_K) {
            marginBottom = 30.px
        }
        color = Colors.textMediumGrey
        + headline4
    }
    val repoMappingSubtitle by css {
        color = Colors.textDarkGrey
        + headline5
        marginBottom = 30.px
        media(QUAD_HD) {
            marginBottom = 80.px
        }
        media(FOUR_K) {
            marginBottom = 80.px
        }
    }

    val repoMappingRepoComponentContainer by css {
        marginBottom = 28.px
        media(QUAD_HD) {
            marginBottom = 40.px
        }
        media(FOUR_K) {
            marginBottom = 40.px
        }
    }

    val repoMappingRepoName by css {
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
//        flexBasis = 30.pct.basis
        textAlign = TextAlign.start
        color = Colors.textMediumGrey
        display = Display.flex
        + bodyFont
    }

    val repoMappingActionButtonContainer by css {
        display = Display.flex
        justifyContent = JustifyContent.flexEnd
        alignItems = Align.center
        width = 100.pct
        marginTop = 40.px
        media(QUAD_HD) {
            marginTop = 100.px
        }
        media(FOUR_K) {
            marginTop = 100.px
        }
    }

    val repoMappingActionButtonHelpText by css {
        width = LinearDimension.fitContent
        display = Display.inlineFlex
        marginRight = 28.px
        + body2Font
        color = Colors.textDarkGrey
    }

    val repoMappingActionButton by css {
        borderRadius = 6.px
        + primaryActionButtonText
        padding(vertical = 12.px, horizontal = 22.px)
        media(QUAD_HD) {
            padding(vertical = 14.px, horizontal = 26.px)
        }
        media(FOUR_K) {
            padding(vertical = 14.px, horizontal = 26.px)
        }
        textTransform = TextTransform.none
    }

    val iconAndLogoutButtonContainer by css {
        height = 6.vh
        media(QUAD_HD) {
            height = 16.vh
        }
        media(FOUR_K) {
            height = 16.vh
        }
    }

    val codeViewPane by css {
        width = LinearDimension.inherit
        height = 80.vh
        + codeFont
    }
}

var styles = CSSBuilder().apply {
    "html" {
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.normal
        color = Colors.textMediumGrey
        overflow = Overflow.hidden
        backgroundColor = Colors.backgroundMediumGrey
    }
    "body" {
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.normal
        color = Colors.textMediumGrey
        overflow = Overflow.hidden
        backgroundColor = Colors.primaryBlack
        margin(0.px)
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
    "::-webkit-scrollbar-thumb" {
        borderRadius = 6.px
        backgroundColor = Colors.primaryBlack
    }
    "::-webkit-scrollbar" {
        width = 10.px
        backgroundColor = Colors.backgroundMediumGrey
    }
//    "::-webkit-scrollbar-track" {
//    }
    /** Fast3r Theme */
    ".ace-fast3r-dark .ace_comment" {
        color = EditorThemeColors.lightGreen
    }
    ".ace-fast3r-dark .ace_gutter" {
        backgroundColor = EditorThemeColors.gutterBackground
        color = EditorThemeColors.gutterColor
    }
    ".ace-fast3r-dark" {
        backgroundColor = EditorThemeColors.editorBackground
        color = EditorThemeColors.editorColor
    }
    ".ace-fast3r-dark .ace_gutter-active-line" {
        borderRightWidth = 4.px
        borderRightStyle = BorderStyle.solid
        borderRightColor = EditorThemeColors.gutterActiveLineBorderColor
    }
    ".ace-fast3r-dark .ace_keyword" {
        color = EditorThemeColors.tokenOrange
    }
    ".ace-fast3r-dark .ace_meta" {
        color = EditorThemeColors.tokenOrange
    }
    ".ace-fast3r-dark .ace_support.ace_constant.ace_property-value" {
        color = EditorThemeColors.tokenOrange
    }
    ".ace-fast3r-dark .ace_string" {
        color = EditorThemeColors.tokenLightBlue
    }
    ".ace-fast3r-dark .ace_storage" {

    }
    ".ace-fast3r-dark .ace_support.ace_class" {
        color = EditorThemeColors.tokenPink
    }
    ".ace-fast3r-dark .ace_support.ace_function" {
        color = EditorThemeColors.tokenPink
    }
    ".ace-fast3r-dark .ace_support.ace_other" {
        color = EditorThemeColors.tokenPink
    }
    ".ace-fast3r-dark .ace_support.ace_type" {
        color = EditorThemeColors.tokenPink
    }

}