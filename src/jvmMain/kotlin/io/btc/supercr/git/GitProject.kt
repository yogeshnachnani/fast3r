package io.btc.supercr.git

import codereview.Project
import io.btc.supercr.db.ProjectRepository
import org.eclipse.jgit.api.Git

class GitProject constructor(
    private val gitUtils: GitUtils,
    private val projectRepository: ProjectRepository
) {
    private val projects = mutableMapOf<String,Pair<Project, Git>>()

    fun getAllProjects(): List<Project> {
        return projectRepository.getAllProjects()
    }

    fun addProject(project: Project) {
        with(project) {
            val git = gitUtils.openRepo(this.localPath)
            projectRepository.addRepo(this)
            projects[id] = Pair(this, git)
        }
    }

    operator fun get(id: String): Pair<Project, Git>? {
        return projects[id]
    }
}