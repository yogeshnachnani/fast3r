package supercr.css

import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Position
import kotlinx.css.background
import kotlinx.css.position
import kotlinx.css.zIndex

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
}