package io.btc.supercr.git

import HOME
import codereview.Project
import git.provider.RepoSummary
import io.btc.supercr.db.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.util.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.util.concurrent.*

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
            ?: projectRepository[id]
                ?.let { project ->
                    val git = gitUtils.openRepo(project.localPath)
                    projects[id] = Pair(project, git)
                    projects[id]
                }
    }
}

class GitProjectCache {
    companion object {
        private val logger = LoggerFactory.getLogger(GitProjectCache::class.java)
        private val directoriesOSXIsHornyAbout = setOf("Applications", "Desktop", "Documents", "Library", "Movies", "Music", "Pictures", "Public")
    }
    private val knownProjectsByProviderPath: ConcurrentHashMap<String, Project> = ConcurrentHashMap(100)

    public fun initCache(rootDir: String = System.getenv(HOME)) {
        GlobalScope.async(Dispatchers.IO) {
            val processMorDirsChannel = Channel<File>(20000)
            val receiveGitDirsChannel = Channel<File>(2000)
            File(rootDir).processDir(processMorDirsChannel, receiveGitDirsChannel)

            launch(Dispatchers.IO) {
                receiveGitDirsChannel.consumeEach {
                    val providerPath = it.openRepoAndGetProviderPath()
                    if (providerPath != null) {
                        knownProjectsByProviderPath[providerPath] = Project(
                            localPath = it.parent,
                            providerPath = providerPath,
                            name = it.parentFile.name
                        )
                    }
                }
            }
            launch(Dispatchers.IO) {
                processMorDirsChannel.consumeEach {
                    it.processDir(processMorDirsChannel, receiveGitDirsChannel)
                }
            }
        }
    }

    public fun getAllKnownProjects() = knownProjectsByProviderPath.toMap()

    public fun getProjectsFor(repos: List<RepoSummary>): Map<RepoSummary, Project> {
        return repos
            .mapNotNull { repoSummary ->
                knownProjectsByProviderPath[repoSummary.full_name]
                    ?.let { project ->
                        Pair(repoSummary, project)
                    }
            }
            .associate { it }
    }

    private suspend fun File.processDir(processSubDirsChannel: Channel<File>, processGitDirChannel: Channel<File>) {
        GlobalScope.async(Dispatchers.IO) {
            listFiles(FileFilter { file -> file.isDirectory })
                ?.forEach { subDir ->
                    if (subDir.shouldProcessDir()) {
                        processSubDirsChannel.send(subDir)
                        if (subDir.endsWith(".git")) {
                            processGitDirChannel.send(subDir)
                        }
                    }
                }
        }
    }

    private fun File.shouldProcessDir(): Boolean {
        return when {
            !this.isDirectory -> {
                logger.info("Found {} which is not a directory", name)
                false
            }
            Files.isSymbolicLink(this.toPath()) -> false
            name.startsWith(".") && !name.endsWith(".git") -> false
            directoriesOSXIsHornyAbout.contains(name) -> false
            else -> true
        }
    }


    private fun File.openRepoAndGetProviderPath(): String? {
        return try {
            FileRepositoryBuilder()
                .setGitDir(this)
                .readEnvironment()
                .findGitDir()
                .build()
                .let {
                    it.getProviderPathFromGitConfig()
                }
        } catch (exception: Exception) {
            logger.warn("Could not get provider for ${this.absolutePath}", exception)
            null
        }
    }

    private fun Repository.getProviderPathFromGitConfig(): String? {
        val regex = Regex("git@github.com:(.*)/(.*)(.git)?")
        val origin = this.config.getString("remote", "origin", "url")
        return if (origin != null) {
            val results = regex.matchEntire(origin)
            results?.groupValues?.subList(1, results.groupValues.size)?.joinToString("/")?.removeSuffix("/")?.removeSuffix(".git")
        } else {
            null
        }
    }
}