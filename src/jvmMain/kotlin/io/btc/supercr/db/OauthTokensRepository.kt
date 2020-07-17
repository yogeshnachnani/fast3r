package io.btc.supercr.db

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.attach
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import kotlin.math.log

data class OauthTokenInfo(
    val login: String,
    val state: String,
    val authToken: String? = null,
    val scope: String? = null,
    val tokenType: String? = null,
    val createdAt: String,
    val updatedAt: String
)

interface OauthTokensDao {

    /**
     * This has close colors too
     */
    @SqlQuery("""
        SELECT login, state, authToken, scope, tokenType, createdAt, updatedAt
        FROM auth_tokens
        WHERE state = :given_state
        ORDER BY createdAt desc 
        LIMIT 1
    """)
    fun retrieveByState(
        @Bind("given_state") state: String
    ): OauthTokenInfo?

    @SqlUpdate("""
        INSERT INTO auth_tokens(login, state, authToken, scope, tokenType)
        VALUES (:login, :state, NULL, NULL, NULL)
    """
    )
    fun createNewTokenRequest(
        @Bind("login") login: String,
        @Bind("state") state: String
    ): Int

    @SqlUpdate("""
        UPDATE auth_tokens
        SET authToken = :authToken, scope = :scope, tokenType = :tokenType, updatedAt = CURRENT_TIMESTAMP
        WHERE state = :state
        """
    )
    fun updateAuthToken(@BindKotlin tokenInfo: OauthTokenInfo): Int

    @SqlQuery("""
        SELECT login, state, authToken, scope, tokenType, createdAt, updatedAt
        FROM auth_tokens
        WHERE authToken is not null
        order by updatedAt desc
        limit 1
    """)
    fun retrieveLatestToken(): OauthTokenInfo?
}


class OauthTokensRepository constructor(
    private val jdbi: Jdbi
){
    fun createNewTokenRequest(login: String, state: String) {
        jdbi.useTransaction<RuntimeException> { handle ->
            val oauthTokensDao: OauthTokensDao = handle.attach()
            oauthTokensDao.createNewTokenRequest(login = login, state = state)
                .also { returnValue ->
                    require(returnValue == 1) {"Could not create token entry for $login"}
                }
        }
    }

    fun retrieveExistingLogin(state: String): OauthTokenInfo? {
        return jdbi.withHandle<OauthTokenInfo?, RuntimeException> { handle ->
            val oauthTokensDao: OauthTokensDao = handle.attach()
            oauthTokensDao.retrieveByState(state)
        }
    }

    fun retrieveLatestToken(): OauthTokenInfo? {
        return jdbi.withHandle<OauthTokenInfo?, RuntimeException> { handle ->
            val oauthTokensDao: OauthTokensDao = handle.attach()
            oauthTokensDao.retrieveLatestToken()
        }
    }

    fun updateAuthToken(tokenInfo: OauthTokenInfo) {
        jdbi.useTransaction<RuntimeException> { handle ->
            val oauthTokensDao: OauthTokensDao = handle.attach()
            oauthTokensDao.updateAuthToken(tokenInfo)
                .also { returnValue ->
                    require(returnValue == 1) {"Could not update tokenInfo for ${tokenInfo.login}"}
                }
        }
    }
}
