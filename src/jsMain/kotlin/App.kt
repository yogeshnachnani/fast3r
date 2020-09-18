import codereview.FileDiffListV2
import codereview.FileLineItem
import codereview.Project
import codereview.ReviewInfo
import codereview.ReviewStorageProvider
import git.provider.PullRequestSummary
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import react.RBuilder
import react.ReactElement
import react.buildElement
import react.dom.render
import react.dom.span
import supercr.css.ComponentStyles
import supercr.css.styles
import supercr.kb.UniversalKeyboardShortcutHandler
import supercr.workflows.codereview.components.commentThread
import supercr.workflows.codereview.components.fileView
import supercr.workflows.codereview.screens.changeSetOverviewScreen
import supercr.workflows.codereview.screens.changeSetReview
import supercr.workflows.gettingstarted.components.loginComponent
import supercr.workflows.gettingstarted.components.repoInit
import supercr.workflows.mainScreen
import supercr.workflows.overview.components.pullRequestList
import kotlin.js.Date

@JsModule("../../../../processedResources/js/main/pull_request_big_one.json")
external val bigPullRequest: dynamic

@JsModule("../../../../processedResources/js/main/file_diffv2_for_big_pull_request.json")
external val fileDiffForBigPullRequest: dynamic

@JsModule("../../../../processedResources/js/main/file_diffv2_for_small_change.json")
external val fileDiffV2ForSmallRequest: dynamic


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
    console.log("We are at ${window.location.href} and ${window.location.search}")
    render(document.getElementById("root"))  {
//        renderLoginView()
//        renderDiffView()
        renderMainScreen()
//        renderChangesetOverviewScreen()
//        renderPullRequests()
//        renderRepoList()
    }
//    renderDiffView()
//    renderGettingStarted()
//    tryOutKeyboardEnabledList()
//    renderFileView()
//    renderComments()
//    tryoutGithubLogin()
}
private fun RBuilder.renderLoginView() {
    StylesProvider {
        attrs {
            injectFirst = true
        }
        loginComponent {
        }
    }
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
    val sampleFileDiff= jsonParser.decodeFromString(FileDiffListV2.serializer(), JSON.stringify(fileDiffV2ForSmallRequest))
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
        Pair(element, handler)
    }
}

private fun RBuilder.renderPullRequests() {
    val samplePullRequestSummary: PullRequestSummary = jsonParser.decodeFromString(PullRequestSummary.serializer(), JSON.stringify(bigPullRequest))
    StylesProvider {
        attrs {
            injectFirst = true
        }
        pullRequestList {
            attrs {
                pullRequests = listOf(
                    Triple(Project("foo", "bar", "btcmain"), samplePullRequestSummary, "ab"),
                    Triple(Project("foo", "bar", "detekt"), samplePullRequestSummary, "ac"))
            }
        }
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

private fun RBuilder.renderRepoList() {
    StylesProvider {
        attrs {
            injectFirst = true
        }
        repoInit {

        }
    }
}

private fun RBuilder.renderChangesetOverviewScreen() {
    val samplePullRequestSummary: PullRequestSummary = jsonParser.decodeFromString(PullRequestSummary.serializer(), JSON.stringify(bigPullRequest))
    val sampleFileDiff = jsonParser.decodeFromString(FileDiffListV2.serializer(), JSON.stringify(fileDiffForBigPullRequest))
    StylesProvider {
        attrs {
            injectFirst = true
        }
        changeSetOverviewScreen {
            pullRequestSummary = samplePullRequestSummary
            fileDiffList = sampleFileDiff
            handleStartReview = {
                console.log("Will start review")
            }
            project = Project("some_path", "some/path", "btcmain")
        }
    }
}

private fun RBuilder.renderDiffView() {
    val samplePullRequestSummary: PullRequestSummary = jsonParser.decodeFromString(PullRequestSummary.serializer(), JSON.stringify(bigPullRequest))
    val sampleFileDiff = jsonParser.decodeFromString(FileDiffListV2.serializer(), JSON.stringify(fileDiffForBigPullRequest))

    StylesProvider {
        attrs {
            injectFirst = true
        }
        changeSetReview {
            pullRequestSummary = samplePullRequestSummary
            reviewInfo = ReviewInfo(rowId = 1, projectIdentifier = "someproject", provider = ReviewStorageProvider.GITHUB, providerId = 123L)
            onReviewDone = { foo, bar ->

            }
            fileDiffList = sampleFileDiff
        }
    }
}

