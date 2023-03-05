package ru.shiftgen.databse.access.tokens

import org.jetbrains.exposed.sql.ResultRow

interface TokensDAO {
    fun ResultRow.toTokenDTO() = TokenDTO(
        login = this[Tokens.login],
        accessToken = this[Tokens.accessToken],
        refreshToken = this[Tokens.refreshToken]
    )

    suspend fun insertToken(token: TokenDTO): Boolean
    suspend fun updateAccessToken(token: TokenDTO): Boolean
    suspend fun updateRefreshToken(token: TokenDTO): Boolean
    suspend fun getToken(login: String): TokenDTO?
    suspend fun getAccessToken(login: String): String?
    suspend fun getRefreshToken(login: String): String?
    suspend fun getRefreshTokenExpiration(login: String): Long?
    suspend fun deleteToken(login: String): Boolean
}