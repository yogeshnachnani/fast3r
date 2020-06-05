import codereview.DiffChangeType
import codereview.Edit
import codereview.FileDiff
import codereview.FileDiffList
import codereview.FileHeader
import react.ReactElement
import react.buildElement
import react.dom.render
import react.dom.span
import supercr.css.styles
import supercr.kb.UniversalKeyboardShortcutHandler
import supercr.kb.components.keyboardEnabledList
import supercr.workflows.codereview.screens.changeSetScreen
import supercr.workflows.gettingstarted.screens.getStartedScreen
import supercr.workflows.mainScreen
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
//    renderDiffView()
//    renderGettingStarted()
//    renderMainScreen()
    tryOutKeyboardEnabledList()
}

private fun getComponentsToRender(names: List<String>): List<Pair<ReactElement, () -> Unit>> {
    return names.map {
        val element = buildElement {
            span {
                + "I am $it"
            }
        }
        val handler: () -> Unit = {
            console.log("Hello, I am $it")
        }
        Pair(element!!, handler)
    }
}

private fun tryOutKeyboardEnabledList() {
    UniversalKeyboardShortcutHandler.init()
    render(document.getElementById("root")) {
        val elements = getComponentsToRender(
            listOf(
                "foo",
                "bar",
                "baaz",
                "quux",
                "blah",
                "bleh",
                "need",
                "to",
                "make",
                "a",
                "longer",
                "list",
                "with",
                "random",
                "new",
                "things",
                "because",
                "I",
                "have",
                "written",
                "the",
                "code",
                "in",
                "such",
                "a",
                "way",
                "only",
                "half",
                "way",
                "through",
                "this exercise",
                "have I realised",
                "that I could have simply",
                "used a prefix",
                "what an idiot",
                "but it's amazing how",
                "the mind works"
            )
        )
        console.log("Got ", elements.size, " number of elements for the list")
        keyboardEnabledList {
            elementsWithHandlers = elements
        }
    }
}

private fun renderMainScreen() {
    render(document.getElementById("root")) {
        mainScreen {

        }
    }
}

private fun renderGettingStarted() {
    render(document.getElementById("root")) {
        getStartedScreen {

        }
    }
}

private fun renderDiffView() {
    render(document.getElementById("root")) {
        changeSetScreen {
            fileDiffList = FileDiffList(
                fileDiffs =  listOf(
                    testModifyDiff,
                    testAddFileDifff
                )
            )
        }
    }
}

private val testModifyDiff = FileDiff(
    rawTextOld = """
                public class FooBarBaz {
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd line");
                    System.out.println("This is 3rd line");
                    System.out.println("This is 4th line");
                  }
                }
            
        """.trimIndent(),
    rawTextNew = """
                public class FooBarBaz {
                  // Adding a comment - which will be a completely new line
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd changed line");
                    System.out.println("This is 3rd changed line");
                    System.out.println("This is a new line");
                    System.out.println("This is 4th line");

                }
        """.trimIndent(),
    diffChangeType = DiffChangeType.MODIFY,
    fileHeader = FileHeader(
        fileNewPath = "kotlin-js/src/commonMain/kotlin/codereview/ChangesetM.kt",
        fileOldPath = "kotlin-js/src/commonMain/kotlin/codereview/Changeset.kt",
        description = "",
        identifier = "xyz1234",
        editList = listOf(
            Edit(beginA = 1, endA = 1, beginB = 1, endB = 2),
            Edit(beginA = 3, endA = 5, beginB = 4, endB = 7),
            Edit(beginA = 6, endA = 8, beginB = 8, endB = 8),
            Edit(beginA = 9, endA = 9, beginB = 9, endB = 10)
        )
    )
)

private val testRenameDiff = FileDiff(
    rawTextOld = """
                public class FooBarBaz {
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd line");
                    System.out.println("This is 3rd line");
                    System.out.println("This is 4th line");
                  }
                }
            
        """.trimIndent(),
    rawTextNew = """
                public class FooBarBaz {
                  // Adding a comment - which will be a completely new line
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd changed line");
                    System.out.println("This is 3rd changed line");
                    System.out.println("This is a new line");
                    System.out.println("This is 4th line");

                }
        """.trimIndent(),
    diffChangeType = DiffChangeType.RENAME,
    fileHeader = FileHeader(
        fileNewPath = "kotlin-js/src/commonMain/kotlin/codereview/Changeset.kt",
        fileOldPath = "kotlin-js/src/commonMain/kotlin/codereview/Changeset2.kt",
        description = "",
        identifier = "abcd1234",
        editList = listOf(
            Edit(beginA = 1, endA = 1, beginB = 1, endB = 2),
            Edit(beginA = 3, endA = 5, beginB = 4, endB = 7),
            Edit(beginA = 6, endA = 8, beginB = 8, endB = 8),
            Edit(beginA = 9, endA = 9, beginB = 9, endB = 10)
        )
    )
)

val testAddFileDifff = FileDiff (
    rawTextOld = null,
    rawTextNew = """
                public class FooBarBaz {
                  // Adding a comment - which will be a completely new line
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd changed line");
                    System.out.println("This is 3rd changed line");
                    System.out.println("This is a new line");
                    System.out.println("This is 4th line");

                }
        """.trimIndent(),
    diffChangeType = DiffChangeType.ADD,
    fileHeader = FileHeader(
        fileNewPath = "kotlin-js/src/commonMain/kotlin/codereview/ChangesetA.kt",
        fileOldPath = "",
        description = "",
        identifier = "newFile1234",
        editList = emptyList()
    )
)

val testRemovedFileDiff = FileDiff (
    rawTextNew = null,
    rawTextOld = """
                public class FooBarBaz {
                  // Adding a comment - which will be a completely new line
                  public static void main() {
                    System.out.println("This is a line");
                    System.out.println("This is 2nd changed line");
                    System.out.println("This is 3rd changed line");
                    System.out.println("This is a new line");
                    System.out.println("This is 4th line");

                }
        """.trimIndent(),
    diffChangeType = DiffChangeType.DELETE,
    fileHeader = FileHeader(
        fileOldPath = "kotlin-js/src/commonMain/kotlin/codereview/Changeset.kt",
        fileNewPath = "",
        description = "",
        identifier = "oldFile1234",
        editList = emptyList()
    )
)
