package io.btc.supercr.api

import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.routing.Routing
import io.ktor.routing.route
import org.slf4j.LoggerFactory

class StaticApi constructor(
    routing: Routing
) : ApiController(routing) {
    companion object {
        private val logger = LoggerFactory.getLogger(StaticApi::class.java)
    }
    override fun initRoutes(routing: Routing) {
        routing {
            static("/static")  {
                files("js")
                default("index.html")
            }
        }
    }

}
