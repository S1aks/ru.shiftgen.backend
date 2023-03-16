package ru.shiftgen.databse.authorization.tokens

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.databse.authorization.users.Users
import ru.shiftgen.plugins.DatabaseFactory.dbQuery
import ru.shiftgen.plugins.JWTGenerator

object Tokens : Table(), TokensDAO {
    internal val login = reference("login", Users.login).uniqueIndex()
    internal val accessToken = varchar("access_token", 256)
    internal val refreshToken = varchar("refresh_token", 50)
    private val refreshTokenExpiration = long("refresh_token_expiration")
    override val primaryKey = PrimaryKey(login, name = "PK_Token_Login")

    override suspend fun insertToken(token: TokenDTO): Boolean = dbQuery {
        Tokens.insert {
            it[login] = token.login
            it[accessToken] = token.accessToken
            it[refreshToken] = token.refreshToken
            it[refreshTokenExpiration] = JWTGenerator.getRefreshExpiration()
        }.insertedCount > 0
    }

    override suspend fun updateAccessToken(token: TokenDTO): Boolean = dbQuery {
        Tokens.update({ login eq token.login }) {
            it[accessToken] = token.accessToken
        } > 0
    }

    override suspend fun updateRefreshToken(token: TokenDTO): Boolean = dbQuery {
        Tokens.update({ login eq token.login }) {
            it[accessToken] = token.accessToken
            it[refreshToken] = token.refreshToken
            it[refreshTokenExpiration] = JWTGenerator.getRefreshExpiration()
        } > 0
    }

    override suspend fun getToken(login: String): TokenDTO? = dbQuery {
        Tokens.select(Tokens.login eq login)
            .singleOrNull()
            ?.toTokenDTO()
    }

    override suspend fun getAccessToken(login: String): String? = dbQuery {
        Tokens.select(Tokens.login eq login)
            .singleOrNull()
            ?.let { it[accessToken] }
    }

    override suspend fun getRefreshToken(login: String): String? = dbQuery {
        Tokens.select(Tokens.login eq login)
            .singleOrNull()
            ?.let { it[refreshToken] }
    }

    override suspend fun getRefreshTokenExpiration(login: String): Long? = dbQuery {
        Tokens.select(Tokens.login eq login)
            .singleOrNull()
            ?.let { it[refreshTokenExpiration] }
    }

    override suspend fun deleteToken(login: String): Boolean = dbQuery {
        Tokens.deleteWhere { Tokens.login eq login } > 0
    }
}