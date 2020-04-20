package io.btc

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.name.Named
import io.btc.crtool.github.GithubApiClient
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl.asyncHttpClient

class CrtoolGuiceModule : AbstractModule() {
    override fun configure() {
        super.configure()
    }

    @Provides
    @Named("github-http-client")
    @Singleton
    fun provideGithubHttpClient(): AsyncHttpClient {
        return asyncHttpClient()
    }

}

fun main(args: Array<String>) {

    println("Hello, World")
    val injector = Guice.createInjector(CrtoolGuiceModule())
    val githubApiClient = injector.getInstance(GithubApiClient::class.java)
    githubApiClient.testApi()

    System.exit(0)
}

