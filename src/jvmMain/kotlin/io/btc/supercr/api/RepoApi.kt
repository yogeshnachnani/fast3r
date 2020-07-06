package io.btc.supercr.api

import git.provider.RepoSummary
import io.btc.supercr.git.GitProjectCache
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

class RepoApi constructor(
    routing: Routing,
    private val gitProjectCache: GitProjectCache
): ApiController(routing) {
    override fun initRoutes(routing: Routing) {
        routing {
            route("/repos")  {
                get("/_all") {
                    call.respond(HttpStatusCode.OK, gitProjectCache.getAllKnownProjects())
                }
                route("/guess") {
                    post {
                        val repos =  call.receive<List<RepoSummary>>()
                        call.respond(HttpStatusCode.OK , gitProjectCache.getProjectsFor(repos))
                    }
                }
            }
        }
    }

}
