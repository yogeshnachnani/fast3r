package io.btc.supercr.db

import codereview.Project
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.attach
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface ProjectDao {
    @SqlUpdate("""
        INSERT INTO project(id ,name, localPath, providerPath)
        VALUES (:id, :name, :localPath, :providerPath)
    """)
    fun insertProject(@BindKotlin project: Project): Int

    @SqlQuery("""
        SELECT * from project 
    """
    )
    fun getAllProjects(): List<Project>

    @SqlQuery("""
        SELECT * from project where localPath  = :localPath
    """
    )
    fun getProjectsByLocalPath(@Bind("localPath") localPath: String): Project?

    @SqlQuery("""
        SELECT * from project where id = :id
    """)
    fun getProjectById(@Bind("id") id: String): Project?
}

class ProjectRepository constructor(
    private val jdbi: Jdbi
) {


    fun addRepo(project: Project) {
        jdbi.useTransaction<RuntimeException> {  handle ->
            val projectDao: ProjectDao = handle.attach()
            projectDao.insertProject(project)
                .also {
                    require(it == 1) {"Could not insert project $project in db"}
                }
        }
    }

    operator fun get(id: String): Project? {
        return jdbi.withHandle<Project? ,RuntimeException> { handle ->
            val projectDao: ProjectDao = handle.attach()
            projectDao.getProjectById(id)
        }
    }

    fun getByLocalPath(localPath: String): Project? {
        return jdbi.withHandle<Project? ,RuntimeException> { handle ->
            val projectDao: ProjectDao = handle.attach()
            projectDao.getProjectsByLocalPath(localPath)
        }
    }

    public fun getAllProjects(): List<Project> {
        return jdbi.withHandle<List<Project> ,RuntimeException> { handle ->
            val projectDao: ProjectDao = handle.attach()
            projectDao.getAllProjects()
        }
    }
}