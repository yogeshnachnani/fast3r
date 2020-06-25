package io.btc.supercr.api

import codereview.DiffChangeType
import codereview.FileDiffList
import codereview.FileDiffListV2
import codereview.FileLineItem
import codereview.Project
import io.btc.supercr.db.FileLineComment
import io.btc.supercr.db.FileLineItemsRepository
import io.btc.supercr.db.FileReviewInfo
import io.btc.supercr.db.FileType
import io.btc.utils.TestUtils
import io.btc.utils.TestUtils.Companion.validBtcRef
import io.btc.utils.clearTestDb
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jdbi.v3.core.Jdbi
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


class ProjectApiTest {
    private val json = Json(configuration = JsonConfiguration.Stable)
    private val testProject = Project(providerPath = "theboringtech/btcmain", localPath = TestUtils.btcRepoDir, name = "BTC")
    private val testRequestAsJson = json.stringify(Project.serializer(), testProject)
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
            val returnedProject = json.parse(Project.serializer(), response.content!!)
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
    fun `testDiff - should return diff `()  = withTestApplication({superCrServer(jdbi)}) {
        addProjectEntry()
        val oldRef = "51664bc83fc398a50d8fcf601d24c9449c95396b"
        val newRef = "f5d172438eab345885a0af297683f7b41a14060f"
        with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/diff?oldRef=$oldRef&newRef=$newRef")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val returnedPayload = json.parse(FileDiffList.serializer(), response.content!!)
            assertEquals(2, returnedPayload.fileDiffs.size )
            assertEquals(1, returnedPayload.fileDiffs.filter { it.diffChangeType == DiffChangeType.MODIFY }.size )
            assertEquals(1, returnedPayload.fileDiffs.filter { it.diffChangeType == DiffChangeType.ADD }.size )
        }
    }

    @Test
    fun `testDiff - should return diff for v2`()  = withTestApplication({superCrServer(jdbi)}) {
        addProjectEntry()
        val oldRef = "51664bc83fc398a50d8fcf601d24c9449c95396b"
        val newRef = "f5d172438eab345885a0af297683f7b41a14060f"
        with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/v2/diff?oldRef=$oldRef&newRef=$newRef")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val returnedPayload = json.parse(FileDiffListV2.serializer(), response.content!!)
            assertEquals(2, returnedPayload.fileDiffs.size )
            assertEquals(1, returnedPayload.fileDiffs.filter { it.diffChangeType == DiffChangeType.MODIFY }.size )
            assertEquals(1, returnedPayload.fileDiffs.filter { it.diffChangeType == DiffChangeType.ADD }.size )
        }
    }

    @Test
    fun `testDiff - should return diff for v2 with comments`()  = withTestApplication({superCrServer(jdbi)}) {
        addProjectEntry()
        val oldRef = "51664bc83fc398a50d8fcf601d24c9449c95396b"
        val newRef = "f5d172438eab345885a0af297683f7b41a14060f"
        val returnedFileDiff = with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/v2/diff?oldRef=$oldRef&newRef=$newRef")) {
            assertEquals(HttpStatusCode.OK, response.status())
            json.parse(FileDiffListV2.serializer(), response.content!!)
        }
        /** Now, create comments in the db */
        val newFileReviewInfo = FileReviewInfo(returnedFileDiff.fileDiffs.first().newFile!!.path, testProject.id, 1L, FileType.NEW_FILE)
        val newFileComment0 = getTestComment(newFileReviewInfo, "Comment 0", 0)
        val newFileComment1 = getTestComment(newFileReviewInfo, "Comment 1", 1)

        val oldFileReviewInfo = FileReviewInfo(returnedFileDiff.fileDiffs.first().oldFile!!.path, testProject.id, 1L, FileType.OLD_FILE)
        val oldFileComment0 = getTestComment(oldFileReviewInfo, "Comment 10", 0)
        val oldFileComment1 = getTestComment(oldFileReviewInfo, "Comment 11", 1)

        val reviewAndComments = mapOf(
            newFileReviewInfo to listOf(newFileComment0, newFileComment1),
            oldFileReviewInfo to listOf(oldFileComment0, oldFileComment1)
        )
        fileLineItemsRepository.addComments(reviewAndComments)
        /** Now retrieve */

        val expectedCommentsOnOldFile = listOf(
            Pair(0, "Comment 10"),
            Pair(1, "Comment 11")
        )

        val expectedCommentsOnNewFile = listOf(
            Pair(0, "Comment 0"),
            Pair(1, "Comment 1")
        )

        with(handleRequest(HttpMethod.Get, "/projects/${testProject.id}/v2/diff?oldRef=$oldRef&newRef=$newRef")) {
            assertEquals(HttpStatusCode.OK, response.status())
            val returnedFileDiffWithComments = json.parse(FileDiffListV2.serializer(), response.content!!)
            val commentDataForOldFile = returnedFileDiffWithComments.fileDiffs.first().oldFile!!.fileLines
                .flatMap { fileLine -> fileLine.lineItems.map { comment -> Pair(fileLine.filePosition!!, (comment as FileLineItem.LineComment).body) } }
            assertEquals(expectedCommentsOnOldFile, commentDataForOldFile)

            val commentDataForNewFile = returnedFileDiffWithComments.fileDiffs.first().newFile!!.fileLines
                .flatMap { fileLine -> fileLine.lineItems.map { comment -> Pair(fileLine.filePosition!!, (comment as FileLineItem.LineComment).body) } }
            assertEquals(expectedCommentsOnNewFile, commentDataForNewFile)
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