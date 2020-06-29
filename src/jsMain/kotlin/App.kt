import codereview.FileDiffListV2
import codereview.FileLineItem
import git.provider.PullRequestSummary
import react.ReactElement
import react.buildElement
import react.dom.render
import react.dom.span
import supercr.css.styles
import supercr.kb.UniversalKeyboardShortcutHandler
import supercr.workflows.gettingstarted.screens.getStartedScreen
import supercr.workflows.mainScreen
import kotlin.browser.document
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import supercr.css.ComponentStyles
import supercr.workflows.codereview.components.commentThread
import supercr.workflows.codereview.components.fileView
import supercr.workflows.codereview.screens.changeSetScreen
import kotlin.js.Date

@JsModule("../../../../processedResources/js/main/pull_request_big_one.json")
external val bigPullRequest: dynamic

@JsModule("../../../../processedResources/js/main/file_diffv2_for_big_pull_request.json")
external val fileDiffForBigPullRequest: dynamic

@JsModule("../../../../processedResources/js/main/file_diffv2_for_small_change.json")
external val fileDiffV2ForSmallRequest: dynamic

fun main () {
    document.head!!.insertAdjacentHTML("afterbegin", "<style>$styles</style>")
    ComponentStyles.inject()
    /**
     * The official documentation specifies that we create a <div id = 'root'> in index.html
     * Somehow, that prevents tests from executing properly (this was after we introduced react)
     * As a workaround, we create the root div element here instead
     * See here for more details - https://stackoverflow.com/questions/61839800/unit-testing-in-kotlin-js/62058511#62058511
     */
    document.body!!.insertAdjacentHTML("afterbegin", "<div id='root' style='height:100vh; width:100vw;'></div>" )
    UniversalKeyboardShortcutHandler.init()
//    renderDiffView()
//    renderGettingStarted()
//    renderMainScreen()
//    tryOutKeyboardEnabledList()
//    renderFileView()
    renderComments()
}

private fun renderComments() {
    val sampleComments = mutableListOf(
        FileLineItem.Comment("This is a single line comment", "2020-06-26T09:44:44.018189Z", "2020-06-26T09:44:44.018189Z", "yogeshnachnani"),
        FileLineItem.Comment("This is a much longer comment so it should ideally span more lines", "2020-06-26T09:44:44.018189Z", "2020-06-26T09:44:44.018189Z", "yogeshnachnani")
    )
    render(document.getElementById("root"))  {
        commentThread {
            attrs {
                comments = sampleComments
                onCommentAdd = { commentBody ->
                    sampleComments.add(
                        FileLineItem.Comment(commentBody, Date().toISOString(), Date().toISOString(), "yogeshnachnani")
                    )
                }
            }
        }
    }
}

private fun renderFileView() {
    val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
    val sampleFileDiff= json.parse(FileDiffListV2.serializer(), JSON.stringify(fileDiffV2ForSmallRequest))
    render(document.getElementById("root"))  {
        fileView {
            fileDiff = sampleFileDiff.fileDiffs.first()
        }
    }
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
    val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
    val samplePullRequestSummary: PullRequestSummary = json.parse(PullRequestSummary.serializer(), JSON.stringify(bigPullRequest))
    val sampleFileDiff = json.parse(FileDiffListV2.serializer(), JSON.stringify(fileDiffForBigPullRequest))
    render(document.getElementById("root")) {
        changeSetScreen {
            pullRequestSummary = samplePullRequestSummary
            fileDiffList = sampleFileDiff
        }
    }
}

