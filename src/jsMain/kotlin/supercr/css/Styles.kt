package supercr.css

import kotlinx.css.Align
import kotlinx.css.BackgroundRepeat
import kotlinx.css.BorderStyle
import kotlinx.css.BoxSizing
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Display
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
import kotlinx.css.VerticalAlign
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
import kotlinx.css.flex
import kotlinx.css.flexBasis
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
import kotlinx.css.properties.boxShadow
import kotlinx.css.properties.lh
import kotlinx.css.px
import kotlinx.css.rgba
import kotlinx.css.right
import kotlinx.css.textAlign
import kotlinx.css.top
import kotlinx.css.verticalAlign
import kotlinx.css.vh
import kotlinx.css.vw
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
    /** Backgrounds */
    val background7 = hsl(0, 0, 38)

    /** Text */
    val baseText4 = hsl(0, 0, 88)
    val baseText3 = hsl(0, 0, 93)
    val baseText = hsl(0, 0, 96)
    val baseText1 = hsl(0, 0, 98)


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
    val gutterBackground = Colors.backgroundDarkestGrey
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
    val huge = 30.px
    val crayCray = 38.px
}
object LineHeights {
    val tiny = 20.px.lh
    val normal = 22.px.lh
    val extraLarge = 32.px.lh
    val large = 28.px.lh
    val huge = 40.px.lh
    val crayCray = 48.px.lh
}

object FontFamilies {
    const val code = "Menlo, Consolas, 'DejaVu Sans Mono', monospace"
    const val nonCode = "Inter, Roboto, Sans-serif"
}

val commentBoxWidth = 312.px
val loginScreenItemsLeftMargin = 36.vw

object ComponentStyles: StyleSheet("SuperCrCss", isStatic = true) {
    val fullHeight by css {
        height = 100.vh
    }

    val keyboardShortcutSingleCharBox by css {
        width = 44.px
        height = 42.px
        backgroundColor = Colors.backgroundDarkestGrey
        borderRadius = 4.px
        fontFamily = FontFamilies.nonCode
        fontStyle = FontStyle.normal
        fontWeight = FontWeight.normal
        fontSize = FontSizes.huge
        lineHeight = LineHeights.huge
        display = Display.flex
        textAlign = TextAlign.center
        justifyContent = JustifyContent.center
        alignItems = Align.center
        alignContent = Align.center
    }

    val genericListHeader by css {
        fontSize = FontSizes.large
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText1
        fontWeight = FontWeight.w700
    }
    val fileListPane by css {
        marginTop = 30.px
        height = 100.vh
        minWidth = 400.px
        paddingRight = 8.px
        marginLeft = 0.px
        marginRight = 0.px
        marginBottom = 0.px
        minWidth = 450.px
    }
    val fileListHeaderItem by css {
        marginTop = 40.px
        marginBottom = 40.px
        color = Colors.textDarkGrey
        paddingLeft = 0.px
        paddingRight = 0.px
        paddingBottom = 0.px
        paddingTop = 0.px
        maxWidth = 480.px
        maxHeight = 60.px
        fontSize = FontSizes.large
        lineHeight = LineHeights.large
    }
    val fileListItem by css {
        paddingLeft = 0.px
        paddingRight = 0.px
        paddingBottom = 0.px
        paddingTop = 0.px
        maxWidth = 480.px
        maxHeight = 60.px
        fontSize = FontSizes.large
        lineHeight = LineHeights.large
    }

    val fileList by css {
        maxWidth = 480.px
        maxHeight = 900.px
        width = 100.pct
        overflow = Overflow.auto
    }

    val fileListTshirtSizePosition by css {
        float = Float.left
        minWidth = 10.pct
        marginRight = 4.px
        marginLeft = 40.px
    }

    val fileListHeaderIcon by css {
        float = Float.left
        minWidth = 10.pct
        marginRight = 4.px
        marginLeft = 32.px
        color = Colors.iconGrey
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
        color = Colors.baseText1
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
        height = 80.px
        backgroundColor = Colors.backgroundDarkestGrey
        fontFamily = FontFamilies.nonCode
        fontStyle = FontStyle.normal
        fontWeight = FontWeight.normal
        fontSize = FontSizes.extraLarge
        lineHeight = LineHeights.extraLarge
        width = 100.pct
        display = Display.block
    }
    val fileViewFileInfoText by css {
        marginTop = 24.px
        marginBottom = 24.px
        marginLeft = 36.px
        color = Colors.textDarkGrey
    }

    val infoPaper by css {
        backgroundColor = Colors.background7
        fontSize = FontSizes.normal
        fontFamily = FontFamilies.nonCode
        color = Colors.baseText1
    }

    val loginScreen by css {
        width = 100.vw
        height = 100.vh
        backgroundImage = Image("url(\"login_screen_background.png\")")
        backgroundPosition = "center"
        backgroundRepeat = BackgroundRepeat.noRepeat
        backgroundSize = "cover"
//        display = Display.flex
//        transform {
//            rotate(( -10.81 ).deg)
//        }
//        opacity = 0.6
        display = Display.inlineBlock
    }
    val loginScreenMessage by css {
        marginLeft = loginScreenItemsLeftMargin
        marginTop = 40.vh
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.huge
        lineHeight = LineHeights.huge
        color = Colors.textLightGrey
    }

    val loginScreenOrMessage by css {
        marginLeft = loginScreenItemsLeftMargin
        marginTop = 50.px
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.huge
        lineHeight = LineHeights.huge
        color = Colors.textLightGrey
        textAlign = TextAlign.center
        width = 800.px
        display = Display.inlineFlex
        justifyContent = JustifyContent.center
//        flex(flexBasis = FlexBasis.maxContent)
    }

    val loginScreenDemoButton by css {
//        backgroundColor = Colors.primaryBlue
        backgroundColor = Color.transparent
        borderRadius = 11.px
        borderColor = Colors.primaryBlue
        borderWidth = 4.px
        borderStyle = BorderStyle.solid
        fontWeight = FontWeight.w600
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.extraLarge
        lineHeight = LineHeights.extraLarge
        color = Colors.textLightGrey
        padding(vertical = 24.px, horizontal = 38.px)
        marginLeft = 24.px
        width = 450.px
    }
    val loginScreenPressCtrlEnterLabel by css {
        width = LinearDimension.fitContent
        fontSize = FontSizes.normal
        lineHeight = LineHeights.normal
        color = Colors.textDarkGrey
        marginTop = 38.px
        marginBottom = 38.px
        marginLeft = 30.px
        display = Display.inlineBlock
    }

    val loginScreenUsernameBoxContainer by css {
        marginLeft = loginScreenItemsLeftMargin
        marginTop = 44.px
        backgroundColor = Colors.backgroundDarkGrey
        borderColor = Colors.backgroundGrey
        borderStyle = BorderStyle.solid
        borderWidth = 1.px
        boxSizing = BoxSizing.borderBox
        borderRadius = 11.px
        width = LinearDimension.fitContent
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
//        width = LinearDimension.fillAvailable
        maxWidth = 445.px
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
        fontSize = FontSizes.normal
        lineHeight = LineHeights.normal
        color = Colors.textDarkGrey
        marginTop = 38.px
        marginBottom = 38.px
        display = Display.inlineBlock
    }
    val loginGo by css {
        backgroundColor = Colors.primaryBlue
        borderTopRightRadius = 11.px
        borderBottomRightRadius = 11.px
        borderTopLeftRadius = 0.px
        borderBottomLeftRadius = 0.px
        fontWeight = FontWeight.w600
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.extraLarge
        lineHeight = LineHeights.extraLarge
        color = Colors.textLightGrey
        padding(vertical = 34.px, horizontal = 38.px)
        marginLeft = 24.px
        width = 120.px
    }

    val repoInitialiserRepoPathInput by css {
        ".MuiOutlinedInput-input" {
            fontSize = FontSizes.normal
            fontFamily = FontFamilies.code
            color = Colors.baseText1
        }
    }

    val extraInfoWindowContainer by css {
        backgroundColor = Colors.backgroundDarkestGrey
        width = 100.pct
        height = 100.pct
        marginTop = 4.px
    }

    val extraInfoWindowHeader by css {
        height = 80.px
        backgroundColor = Colors.primaryBlack
        width = 100.pct
    }

    val actionBar by css {
        position = Position.absolute
        right = 2.px
        bottom = 30.vh
        backgroundColor = Colors.backgroundDarkestGrey
        borderRadius = 14.px
        fontSize = FontSizes.normal
        fontFamily = FontFamilies.nonCode
        lineHeight = LineHeights.normal
        color = Colors.textDarkGrey
        width = 1080.px
        height = 80.px
        display = Display.flex
        justifyContent = JustifyContent.flexEnd
        alignContent = Align.flexEnd
    }
    val actionBarKeyboardLetterBox by css {
        width = 44.px
        height = 42.px
        backgroundColor = Colors.backgroundGrey
        borderRadius = 4.px
        fontFamily = FontFamilies.nonCode
        fontStyle = FontStyle.normal
        fontWeight = FontWeight.normal
        fontSize = FontSizes.huge
        lineHeight = LineHeights.huge
        display = Display.flex
        textAlign = TextAlign.center
        justifyContent = JustifyContent.center
        alignItems = Align.center
        alignContent = Align.center
        marginTop = 20.px
        marginBottom = 20.px
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
        fontSize = FontSizes.huge
        lineHeight = LineHeights.huge
        fontFamily = FontFamilies.nonCode
        color = Colors.textDarkGrey
    }

    val pullRequestSummaryCard by css {
        backgroundColor = Colors.backgroundDarkGrey
        borderRadius = 8.px
        boxShadow(color = rgba(0, 0, 0, 0.25), offsetX = 0.px, offsetY = 4.px, blurRadius = 74.px )
        margin(0.px)
        padding(0.px)
    }

    val pullRequestSummaryCardHeading by css {
        marginTop = 24.px
        marginLeft = 24.px
        marginBottom = 0.px
        fontSize = FontSizes.extraLarge
        lineHeight = LineHeights.extraLarge
        color = Colors.textMediumGrey
    }
    val pullRequestSummaryHeaderContainer by css {
        display = Display.inlineFlex
        justifyContent = JustifyContent.spaceBetween
        width = 100.pct
        margin(0.px)
    }

    val pullRequestSummaryAgeRibbon by css {
        borderTopLeftRadius = 15.px
        borderBottomLeftRadius = 15.px
        borderTopRightRadius = 0.px
        borderBottomRightRadius = 0.px
        height = 36.px
//        padding(vertical = 6.px, horizontal = 20.px)
        marginTop = 24.px
        color = Colors.textLightGrey
        fontSize = FontSizes.normal
        lineHeight = LineHeights.normal
        fontFamily = FontFamilies.nonCode
        verticalAlign = VerticalAlign.middle
        textAlign = TextAlign.center
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
        fontSize = FontSizes.normal
        lineHeight = LineHeights.normal
        fontFamily = FontFamilies.nonCode
        color = Colors.textLightGrey
        marginLeft = 24.px
        marginTop = 12.px
        marginBottom = 12.px
        display = Display.block
        padding(vertical = 6.px, horizontal = 12.px)
    }

    val pullRequestSummaryCommentContainer by css {
        borderRadius = 5.px
        backgroundColor = Colors.backgroundDarkestGrey
        marginLeft = 24.px
        marginRight = 24.px
        marginBottom = 16.px
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
        color = Colors.textDarkGrey
        fontSize = FontSizes.normal
        lineHeight = LineHeights.normal
        fontFamily = FontFamilies.nonCode
        position = Position.relative
    }

    val pullRequestSummaryCommentBody by css {
        height = LinearDimension(LineHeights.normal.value).times(2)
    }

    val pullRequestSummaryCommentUserAvatar by css {
        position = Position.absolute
        display = Display.inlineFlex
        top = 24.px
        left = 24.px
    }

    val pullRequestMetaDataContainer by css {
        marginLeft = 24.px
        marginRight = 24.px
        paddingBottom = 14.px
        display = Display.flex
        justifyContent = JustifyContent.spaceBetween
        alignContent = Align.center
    }

    val pullRequestMetaDataItems by css {
        width = LinearDimension.fitContent
        display = Display.inlineFlex
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
        flex(flexBasis = 40.pct.basis)
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
        flexBasis = 60.pct.basis
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
    }

    val pullRequestSummaryMetaDataSize by css {
        display = Display.inlineFlex
        fontSize = FontSizes.normal
        color = Colors.primaryBlue
        justifyContent = JustifyContent.flexStart
        alignItems = Align.center
    }

    val pullRequestSummaryCardKeyboardShortcut by css {
        width = 44.px
        height = 42.px
        backgroundColor = Colors.backgroundGrey
        borderRadius = 4.px
        fontFamily = FontFamilies.nonCode
        fontStyle = FontStyle.normal
        fontWeight = FontWeight.normal
        fontSize = FontSizes.huge
        lineHeight = LineHeights.huge
        display = Display.flex
        textAlign = TextAlign.center
        justifyContent = JustifyContent.center
        alignItems = Align.center
        alignContent = Align.center
        color = Colors.textMediumGrey
    }

    val avatarInitials by css {
        width = 34.px
        height = 34.px
        backgroundColor = EditorThemeColors.tokenOrange
        fontSize = FontSizes.normal
        lineHeight = LineHeights.normal
    }
}

var styles = CSSBuilder().apply {
    "html" {
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.normal
        color = Colors.baseText1
        overflow = Overflow.hidden
        backgroundColor = Colors.backgroundMediumGrey
    }
    "body" {
        fontFamily = FontFamilies.nonCode
        fontSize = FontSizes.normal
        color = Colors.baseText1
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