package io.btc.supercr.api

import codereview.FileDiffListV2
import codereview.Project
import git.provider.PullRequestSummary
import io.btc.supercr.db.ReviewInfo
import io.btc.supercr.db.ReviewStorageProvider
import io.btc.supercr.db.toReviewInfo
import io.btc.supercr.git.GitProject
import io.btc.supercr.git.checkOrFetchRef
import io.btc.supercr.git.fetchRef
import io.btc.supercr.git.formatDiff
import io.btc.supercr.git.formatDiffV2
import io.btc.supercr.review.ReviewController
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

class ProjectApi constructor(
    routing: Routing,
    private val gitProject: GitProject,
    private val reviewController: ReviewController
): ApiController(routing) {

    override fun initRoutes(routing: Routing) {
        routing {
            route("/projects")  {
                get("_all") {
                    call.respond(gitProject.getAllProjects())
                }
                post {
                    with(call.receive<Project>()) {
                        gitProject.addProject(this)
                    }
                    call.respond(HttpStatusCode.Created ,mapOf("OK" to true))
                }
                route("{id}") {
                    get {
                        val id = call.parameters["id"]!!
                        val responseProject = (gitProject[id]?.first)
                            ?: Project(providerPath = "Not found", localPath = id, name = "NA")
                        call.respond(responseProject)
                    }
                    post("fetch/{ref}") {
                        val (project, git) = gitProject[call.parameters["id"]!!] ?: Pair(null, null)
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
                        val (project, git) = gitProject[call.parameters["id"]!!] ?: Pair(null, null)
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
                    route("review") {
                        post {
                            val projectId = call.parameters["id"]!!
                            /** First ensure that the project is present in the db */
                            if (gitProject[projectId] == null) {
                                call.respond(HttpStatusCode.NotFound)
                            } else {
                                val reviewInfo = with(call.receive<PullRequestSummary>()) {
                                    this.toReviewInfo(projectId)
                                }
                                reviewController.getOrCreateReview(reviewInfo)
                                    .also { reviewInfoFromDb ->
                                        call.respond(HttpStatusCode.Created ,reviewInfoFromDb)
                                    }
                            }
                        }
                        route("{review_id}") {
                            get {
                                val oldRef = call.request.queryParameters["oldRef"]
                                val newRef = call.request.queryParameters["newRef"]
                                val reviewId = call.parameters["review_id"]!!.toLong()
                                val projectIdentifier = call.parameters["id"]!!
                                val (project, git) = gitProject[projectIdentifier] ?: Pair(null, null)
                                val reviewInfo = reviewController.fetchReview(reviewId)
                                if (oldRef == null || newRef == null || project == null || reviewInfo == null) {
                                    call.respond(HttpStatusCode.BadRequest)
                                } else {
                                    val fetchedOldRef = git!!.checkOrFetchRef(oldRef)
                                    val fetchedNewRef = git.checkOrFetchRef(newRef)
                                    if(!fetchedOldRef || !fetchedNewRef) {
                                        call.respond(HttpStatusCode.NotFound)
                                    } else {
                                        val gitDiff =  git.formatDiffV2(oldRef, newRef)
                                        val diffWithComments = reviewController.retrieveCommentsFor(gitDiff, reviewInfo.rowId!!)
                                        call.respond(diffWithComments)
                                    }
                                }
                            }
                            post {
                                val reviewId = call.parameters["review_id"]!!.toLong()
                                with(call.receive<FileDiffListV2>()) {
                                    reviewController.storeCommentsFor(this, reviewId)
                                }
                                call.respond(HttpStatusCode.OK)
                            }
                        }
                    }

                }
            }
        }
    }
}

