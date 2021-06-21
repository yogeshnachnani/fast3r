package io.btc.supercr.api

import codereview.DiffChangeType
import codereview.DiffTShirtSize
import codereview.FileDiffListV2
import codereview.FileLineItem
import codereview.Project
import codereview.ReviewInfo
import codereview.retrieveAllLineItems
import io.btc.supercr.db.FileLineItemsRepository
import io.btc.supercr.db.FileReviewInfo
import io.btc.utils.TestUtils
import io.btc.utils.TestUtils.Companion.validBtcRef
import io.btc.utils.clearTestDb
import io.btc.utils.getCurrentTimeInIsoDateTime
import io.btc.utils.getTestComment
import io.btc.utils.initTestDb
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import jsonParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jdbi.v3.core.Jdbi
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class ProjectApiTest {
    private val testProject = Project(providerPath = "theboringtech/btcmain", localPath = TestUtils.btcRepoDir, name = "BTC")
    private val testRequestAsJson = jsonParser.encodeToString(Project.serializer(), testProject)
    private lateinit var jdbi: Jdbi
    private lateinit var fileLineItemsRepository: FileLineItemsRepository

    @Before
    fun setUp() {
        if (! this::jdbi.isInitialized) {
            jdbi = initTestDb()
        }
        jdbi.clearTestDb()
        fileLineItemsRepository = FileLineItemsRepository(jdbi)
    }

    @Test
    fun testAddAndRetrieveProject() = withTestApplication({superCrServer(jdbi)}) {
        /** Create project */
        addProjectEntry()
        /** Get Project */
        with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}")) {
            val returnedProject = jsonParser.decodeFromString(Project.serializer(), response.content!!)
            assertEquals(testProject, returnedProject)
        }
    }

    @Test
    fun `testFetch - should return NOT FOUND if project not found missing`() = withTestApplication({superCrServer(jdbi)}) {
        with(handleRequest(HttpMethod.Post, "/projects/foo/fetch/foobar"))  {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @Test
    fun `testFetch - should fetch the ref from upstream`() = withTestApplication({superCrServer(jdbi)}) {
        addProjectEntry()
        with(handleRequest(HttpMethod.Post, "/projects/${testProject.id}/fetch/$validBtcRef"))  {
            assertEquals(HttpStatusCode.Accepted, response.status())
        }
    }

    @Test
    fun `testFetch - should return NOT FOUND if ref is not found`() = withTestApplication({superCrServer(jdbi)}) {
        /** Create Test Repo */
        addProjectEntry()
        /** Fetch non existent ref */
        with(handleRequest(HttpMethod.Post, "/projects/${testProject.id}/fetch/foobar"))  {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @Test
    fun `testDiff - should return BAD REQUEST if oldRef or newRef parameters are missing`()  = withTestApplication({superCrServer(jdbi)})  {
        addProjectEntry()
        with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/diff")) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun `testDiff - should return FileDiff given oldRef and newRef`() = withTestApplication({superCrServer(jdbi)})  {
        addProjectEntry()
        val oldRef = "51664bc83fc398a50d8fcf601d24c9449c95396b"
        val newRef = "f5d172438eab345885a0af297683f7b41a14060f"
        val returnedFileDiff = with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/diff?oldRef=$oldRef&newRef=$newRef")) {
            assertEquals(HttpStatusCode.OK, response.status())
            jsonParser.decodeFromString(FileDiffListV2.serializer(), response.content!!)
        }
        assertEquals(2, returnedFileDiff.fileDiffs.size )
        assertEquals(DiffTShirtSize.S,returnedFileDiff.diffTShirtSize)
    }

    @Test
    fun `testReview - should throw not found if review requested for non existent project`() = withTestApplication({superCrServer(jdbi)}) {
        with(handleRequest(HttpMethod.Post, "/projects/foo/review") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(TestUtils.samplePullRequestSummaryJson)
        }) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @Test
    fun `testReview - should create a fresh review for a given PR`()= withTestApplication({superCrServer(jdbi)}) {
        addProjectEntry()
        val createdReviewInfo = createReviewInfo()
        assertTrue(createdReviewInfo.rowId != null)
    }

    @Test
    fun `testReview - should retrieve existing review for a given PR if already registered`() = withTestApplication({superCrServer(jdbi)}){
        addProjectEntry()
        val createdReviewInfo = createReviewInfo()

        val secondTimeReviewInfo = with(handleRequest(HttpMethod.Post, "/projects/${testProject.id}/review") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(TestUtils.samplePullRequestSummaryJson)
        }) {
            jsonParser.decodeFromString(ReviewInfo.serializer(), response.content!!)
        }
        assertEquals(createdReviewInfo, secondTimeReviewInfo)
    }

    @Test
    fun `testReview - should return diff without comments`()  = withTestApplication({superCrServer(jdbi)}) {
        addProjectEntry()
        val createdReview = createReviewInfo()
        val oldRef = "51664bc83fc398a50d8fcf601d24c9449c95396b"
        val newRef = "f5d172438eab345885a0af297683f7b41a14060f"
        val returnedFileDiff = with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/review/${createdReview.rowId!!}?oldRef=$oldRef&newRef=$newRef")) {
            assertEquals(HttpStatusCode.OK, response.status())
            jsonParser.decodeFromString(FileDiffListV2.serializer(), response.content!!)
        }

        /** Basic sanity check - because I'm too lazy to write 2 tests for retrieval */
        assertEquals(2, returnedFileDiff.fileDiffs.size )
        assertEquals(1, returnedFileDiff.fileDiffs.filter { it.diffChangeType == DiffChangeType.MODIFY }.size )
        assertEquals(1, returnedFileDiff.fileDiffs.filter { it.diffChangeType == DiffChangeType.ADD }.size )

        /** Verify we have no comments */
        assertTrue(returnedFileDiff.fileDiffs[0].oldFile!!.retrieveAllLineItems().isEmpty())
        assertTrue(returnedFileDiff.fileDiffs[0].newFile!!.retrieveAllLineItems().isEmpty())
        assertTrue(returnedFileDiff.fileDiffs[1].oldFile!!.retrieveAllLineItems().isEmpty())
        assertTrue(returnedFileDiff.fileDiffs[1].newFile!!.retrieveAllLineItems().isEmpty())
    }

    @Test
    fun `testReview - should store and retrieve comments`()  = withTestApplication({superCrServer(jdbi)}) {
        addProjectEntry()
        val createdReview = createReviewInfo()
        val oldRef = "51664bc83fc398a50d8fcf601d24c9449c95396b"
        val newRef = "f5d172438eab345885a0af297683f7b41a14060f"
        val returnedFileDiff = with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/review/${createdReview.rowId!!}?oldRef=$oldRef&newRef=$newRef")) {
            assertEquals(HttpStatusCode.OK, response.status())
            jsonParser.decodeFromString(FileDiffListV2.serializer(), response.content!!)
        }

        /** Now, create comments in the db */
        val newfileLinesForNewFile = returnedFileDiff.fileDiffs.first().newFile!!.fileLines.mapIndexed { index, fileLine ->
            if (fileLine.filePosition != null) {
                fileLine.copy(lineItems = listOf(FileLineItem.Comment("Comment on $index", getCurrentTimeInIsoDateTime(), getCurrentTimeInIsoDateTime(), "user1")))
            } else {
                fileLine
            }
        }
        val newDiffWithComments = returnedFileDiff.fileDiffs.first().copy(
            newFile = returnedFileDiff.fileDiffs.first().newFile!!.copy(fileLines = newfileLinesForNewFile)
        )
        val completeDiffWithComments = returnedFileDiff.copy(fileDiffs = listOf(newDiffWithComments, returnedFileDiff.fileDiffs[1]))

        with(handleRequest(HttpMethod.Post, "/projects/${testProject.id}/review/${createdReview.rowId}") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(jsonParser.encodeToString(FileDiffListV2.serializer(), completeDiffWithComments))
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        val returnedFileDiffWithComments = with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/review/${createdReview.rowId!!}?oldRef=$oldRef&newRef=$newRef")) {
            assertEquals(HttpStatusCode.OK, response.status())
            jsonParser.decodeFromString(FileDiffListV2.serializer(), response.content!!)
        }
        val commentBodies = returnedFileDiffWithComments.fileDiffs[0].newFile!!
            .retrieveAllLineItems()
            .flatMap { (_, _, comments) ->
                comments.map { (it as FileLineItem.Comment).body }
            }
        assertTrue(commentBodies.all { it.startsWith("Comment on") })

    }

    private fun TestApplicationEngine.createReviewInfo(): ReviewInfo {
        return with(handleRequest(HttpMethod.Post, "/projects/${testProject.id}/review") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(TestUtils.samplePullRequestSummaryJson)
        }) {
            jsonParser.decodeFromString(ReviewInfo.serializer(), response.content!!)
        }
    }


    private fun TestApplicationEngine.addProjectEntry() {
        with(handleRequest(HttpMethod.Post, "/projects") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(testRequestAsJson)
        }) {
            assertEquals(HttpStatusCode.Created, response.status())
        }
    }
}