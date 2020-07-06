package io.btc.supercr.api

import APP_NAME
import DEFAULT_PORT
import HOME
import io.btc.supercr.db.FileLineItemsRepository
import io.btc.supercr.db.ProjectRepository
import io.btc.supercr.git.GitProject
import io.btc.supercr.git.GitProjectCache
import io.btc.supercr.git.GitUtils
import io.btc.supercr.review.ReviewController
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlite3.SQLitePlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.slf4j.event.Level
import java.io.File


class ApiServer constructor(

) {
    init {
        embeddedServer(Netty, DEFAULT_PORT, watchPaths = listOf("ApiServerInitKt"), module = Application::superCrServerProduction)
            .start()
    }

}

fun Application.superCrServerProduction() {
    this.superCrServer(initDb())
}

fun Application.superCrServer(jdbi: Jdbi, isProduction: Boolean = true) {
    val gitProjectCache = GitProjectCache()
    if (isProduction) {
        gitProjectCache.initCache()
    }
    install(DefaultHeaders)
    install(CallLogging) {
        level = Level.INFO
    }
    install(ContentNegotiation) {
        json(json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)))
    }
    install(Routing) {
        get("/") {
            call.respond(mapOf("OK" to true))
        }
        ProjectApi(this, GitProject(GitUtils(), ProjectRepository(jdbi)), ReviewController(FileLineItemsRepository(jdbi)))
        RepoApi(this, gitProjectCache)
    }
    install(CORS) {
        host("localhost:8080")
        allowNonSimpleContentTypes = true
    }
}

fun initDb(dbName: String = "supercrdb"): Jdbi {
    val userHome = System.getenv(HOME)
    val superCrDirectory = "$userHome/.$APP_NAME"
        .also {
            File(it).mkdirs()
        }

    return Jdbi.create("jdbc:sqlite:$superCrDirectory/${dbName}.db")
        .installPlugin(SQLitePlugin())
        .installPlugin(KotlinPlugin())
        .installPlugin(KotlinSqlObjectPlugin())
        .also { jdbi ->
            jdbi.runMigrations()
        }
}

internal fun Jdbi.runMigrations() {
    this.useTransaction<RuntimeException> { handle ->
        handle.createUpdate("""
                CREATE TABLE IF NOT EXISTS project(id TEXT UNIQUE,name TEXT, localPath TEXT, providerPath TEXT)
            """.trimIndent())
            .execute()
        handle.createUpdate("""
                CREATE TABLE IF NOT EXISTS file_line_comments(
                    fileReviewId TEXT, 
                    rowNumber TEXT, 
                    body TEXT,
                    createdAt TEXT,
                    updatedAt TEXT,
                    userId TEXT)
            """.trimIndent())
            .execute()
        handle.createUpdate("""
                CREATE TABLE IF NOT EXISTS file_review_info (
                    path TEXT, 
                    reviewId INTEGER, 
                    fileType TEXT)
            """.trimIndent())
            .execute()
        handle.createUpdate("""
            CREATE UNIQUE INDEX IF NOT EXISTS uk_file_review_info_all on file_review_info(path, reviewId, fileType)
            """.trimIndent())
            .execute()
        handle.createUpdate("""
                CREATE TABLE IF NOT EXISTS review_info (
                    projectIdentifier TEXT, 
                    provider TEXT, 
                    providerId INTEGER)
            """.trimIndent())
            .execute()
        handle.createUpdate("""
            CREATE UNIQUE INDEX IF NOT EXISTS uk_review_info_all on review_info(projectIdentifier, provider, providerId)
        """.trimIndent())
            .execute()
    }
}
