import kotlinx.browser.document
import react.RBuilder
import react.dom.render
import supercr.css.ComponentStyles
import supercr.css.styles
import supercr.kb.UniversalKeyboardShortcutHandler
import supercr.workflows.mainScreen

fun main ()  {
    document.head!!.insertAdjacentHTML("beforeend", "<style>$styles</style>")
    ComponentStyles.inject()
    /**
     * The official documentation specifies that we create a <div id = 'root'> in index.html
     * Somehow, that prevents tests from executing properly (this was after we introduced react)
     * As a workaround, we create the root div element here instead
     * See here for more details - https://stackoverflow.com/questions/61839800/unit-testing-in-kotlin-js/62058511#62058511
     */
    document.body!!.insertAdjacentHTML("beforeend", "<div id='root' style='height:100vh; width:100vw;'></div>" )
    UniversalKeyboardShortcutHandler.init()
    render(document.getElementById("root"))  {
        renderMainScreen()
    }
}

private fun RBuilder.renderMainScreen() {
    StylesProvider {
        attrs {
            injectFirst = true
        }
        mainScreen {

        }
    }
}
