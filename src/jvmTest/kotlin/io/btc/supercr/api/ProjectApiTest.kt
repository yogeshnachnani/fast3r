package io.btc.supercr.api

import codereview.DiffChangeType
import codereview.FileDiffList
import codereview.FileDiffListV2
import codereview.Project
import io.btc.utils.TestUtils
import io.btc.utils.TestUtils.Companion.validBtcRef
import io.btc.utils.clearTestDb
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

    @Before
    fun setUp() {
        if (! this::jdbi.isInitialized) {
            jdbi = initTestDb()
        }
        jdbi.clearTestDb()
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

    private fun TestApplicationEngine.addProjectEntry() {
        with(handleRequest(HttpMethod.Post, "/projects") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(testRequestAsJson)
        }) {
            assertEquals(HttpStatusCode.Created, response.status())
        }
    }
}