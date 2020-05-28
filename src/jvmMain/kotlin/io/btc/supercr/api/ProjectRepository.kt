package io.btc.supercr.api

import codereview.Project
import io.ktor.util.InternalAPI
import org.eclipse.jgit.api.Git

class ProjectRepository {
    private val projects = mutableMapOf<String,Pair<Project, Git>>()

    @InternalAPI
    public fun addRepo(project: Project, git: Git) {
        with(project) {
            if(!projects.containsKey(this.id.toString())) {
                projects[this.id.toString()] = Pair(this, git)
            }
        }
    }

    operator fun get(id: String): Pair<Project, Git>? {
        return projects[id]
    }

    public fun getAllProjects(): Map<String, Project> {
        return projects.mapValues { it.value.first }
    }
}