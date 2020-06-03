package codereview

import DEFAULT_PORT
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class SuperCrClient(
    private val httpClient: HttpClient
) {
    companion object {
        private val baseUrl = "http://localhost:${DEFAULT_PORT}"
        private val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
//        private val json = io.ktor.client.features.json.defaultSerializer()
    }

    suspend fun getAllProjects(): List<Project> {
        return httpClient.request<List<Project>>(buildRequest().apply {
            url("$baseUrl/projects/_all")
            method = HttpMethod.Get
        })
    }

    suspend fun addProject(project: Project): Boolean {
        val response = httpClient.post<HttpResponse> {
            body = project
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url("$baseUrl/projects")
        }
        return when(response.status) {
            HttpStatusCode.Created -> true
            HttpStatusCode.InternalServerError -> false
            HttpStatusCode.BadRequest -> false
            else -> {
                throw RuntimeException("Unhandled status code ${response.status}")
            }
        }
    }

    private suspend fun buildRequest(): HttpRequestBuilder {
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }
    }
    private suspend fun buildPostRequest(): HttpRequestBuilder {
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }
}