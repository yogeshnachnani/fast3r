package io.btc.supercr.api

import codereview.Project
import io.btc.supercr.git.GitUtils
import io.btc.supercr.git.checkOrFetchRef
import io.btc.supercr.git.fetchRef
import io.btc.supercr.git.formatDiff
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.client.request.request
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.application
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.util.InternalAPI
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository

class ProjectApi constructor(
    routing: Routing,
    private val gitUtils: GitUtils,
    private val projectRepository: ProjectRepository
): ApiController(routing) {

    @InternalAPI
    override fun initRoutes(routing: Routing) {
        routing {
            route("/projects")  {
                get("_all") {
                    call.respond(projectRepository.getAllProjects())
                }
                post {
                    with(call.receive<Project>()) {
                        val git = gitUtils.openRepo(this.localPath)
                        projectRepository.addRepo(this, git)
                    }
                    call.respond(mapOf("OK" to true))
                }
                route("{id}") {
                    get {
                        val id = call.parameters["id"]!!
                        val responseProject = (projectRepository[id]?.first)
                            ?: Project(providerPath = "Not found", localPath = id, name = "NA")
                        call.respond(responseProject)
                    }
                    post("fetch/{ref}") {
                        val (project, git) = projectRepository[call.parameters["id"]!!] ?: Pair(null, null)
                        when(project) {
                            null -> call.respond(HttpStatusCode.NotFound)
                            else -> {
                                val ref = call.parameters["ref"]!!
                                if (git!!.fetchRef(ref)) {
                                    call.respond(HttpStatusCode.Accepted)
                                } else {
                                    call.respond(HttpStatusCode.NotFound)
                                }
                            }
                        }
                    }
                    get("diff") {
                        val oldRef = call.request.queryParameters["oldRef"]
                        val newRef = call.request.queryParameters["newRef"]
                        val (project, git) = projectRepository[call.parameters["id"]!!] ?: Pair(null, null)
                        if (oldRef == null || newRef == null || project == null) {
                            call.respond(HttpStatusCode.BadRequest)
                        } else {
                            val fetchedOldRef = git!!.checkOrFetchRef(oldRef)
                            val fetchedNewRef = git.checkOrFetchRef(newRef)
                            if(!fetchedOldRef || !fetchedNewRef) {
                                call.respond(HttpStatusCode.NotFound)
                            } else {
                                call.respond(git.formatDiff(oldRef, newRef))
                            }
                        }
                    }
                }
            }
        }
    }
}

