package io.btc.supercr.api

import io.btc.supercr.git.GitUtils
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level


class ApiServer constructor(

) {
    init {
        embeddedServer(Netty, 8081, watchPaths = listOf("ApiServerInitKt"), module = Application::superCrServer)
            .start()
    }
}

fun Application.superCrServer() {
    install(DefaultHeaders)
    install(CallLogging) {
        level = Level.INFO
    }
    install(ContentNegotiation) {
        jackson {

        }
    }
    install(Routing) {
        get("/") {
            call.respond(mapOf("OK" to true))
        }
        ProjectApi(this, GitUtils(), ProjectRepository())
    }
}
