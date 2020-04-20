package io.btc.crtool.github

import com.google.common.net.HttpHeaders
import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.inject.name.Named
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.ListenableFuture
import org.asynchttpclient.Response

@Singleton
class GithubApiClient @Inject constructor(
    @Named("github-http-client") val githubApiClient: AsyncHttpClient
) {
    companion object {
        val GITHUB_API_VERSION_HEADER = "application/vnd.github.v3+json"
        val GITHUB_BASE_URL = "https://api.github.com/"
    }

    fun testApi() {
        println("Preparing to hit github")
        doGet("orgs/octokit/repos")
            .get()
            .responseBody
            .let {
                println(it)
            }

    }

    private fun doGet(path: String): ListenableFuture<Response> {
        return githubApiClient
            .prepareGet("$GITHUB_BASE_URL$path")
            .addHeader(HttpHeaders.ACCEPT, GITHUB_API_VERSION_HEADER)
            .execute()
    }
}