import codereview.Edit
import react.dom.render
import supercr.views.diffView
import supercr.css.styles
import supercr.views.getStartedScreen
import kotlin.browser.document

fun main () {
    document.head!!.insertAdjacentHTML("afterbegin", "<style>$styles</style>")
    /**
     * The official documentation specifies that we create a <div id = 'root'> in index.html
     * Somehow, that prevents tests from executing properly (this was after we introduced react)
     * As a workaround, we create the root div element here instead
     * See here for more details - https://stackoverflow.com/questions/61839800/unit-testing-in-kotlin-js/62058511#62058511
     */
    document.body!!.insertAdjacentHTML("afterbegin", "<div id='root'></div>" )
    renderDiffView()
//    renderGettingStarted()
}

private fun renderGettingStarted() {
    render(document.getElementById("root")) {
        getStartedScreen {

        }
    }
}

private fun renderDiffView() {
    render(document.getElementById("root")) {
        diffView {
            oldText = """
                public class FooBarBaz {
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd line");
                    System.out.println("This is 3rd line");
                    System.out.println("This is 4th line");
                  }
                }
                
            """.trimIndent()
            newText = """
                public class FooBarBaz {
                  // Adding a comment - which will be a completely new line
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd changed line");
                    System.out.println("This is 3rd changed line");
                    System.out.println("This is a new line");
                    System.out.println("This is 4th line");

                }
            """.trimIndent()
            editList = listOf(
                Edit(beginA = 1, endA = 1, beginB = 1, endB = 2),
                Edit(beginA = 3, endA = 5, beginB = 4, endB = 7),
                Edit(beginA = 6, endA = 8, beginB = 8, endB = 8),
                Edit(beginA = 9, endA = 9, beginB = 9, endB = 10)
            )
        }
    }
}

