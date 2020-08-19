package io.btc.supercr.api

import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.routing.Routing
import io.ktor.routing.application
import org.slf4j.LoggerFactory
import java.io.File

class StaticApi(
    routing: Routing
) : ApiController(routing) {
    companion object {
        private val logger = LoggerFactory.getLogger(StaticApi::class.java)
    }
    override fun initRoutes(routing: Routing) {
        val customResourcePath = System.getProperty("resourcePath")
        routing {
            static("/static")  {
                if (!customResourcePath.isNullOrEmpty()) {
                    staticRootFolder = File("$customResourcePath/lib")
                    logger.info("Serving from {}", staticRootFolder?.absolutePath)
                }
                files("js")
                default("index.html")
            }
        }
    }

}
