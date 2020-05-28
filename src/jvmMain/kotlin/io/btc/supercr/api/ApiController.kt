package io.btc.supercr.api

import io.ktor.routing.Routing

abstract class ApiController(routing: Routing) {
    init {
        initRoutes(routing)
    }
    abstract fun initRoutes(routing: Routing)
}