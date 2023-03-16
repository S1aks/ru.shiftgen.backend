package ru.shiftgen.databse.authorization.tokens

import org.jetbrains.exposed.sql.ResultRow
import ru.shiftgen.plugins.JWTGenerator
import java.util.*

interface TokensDAO {
    fun ResultRow.toTokenDTO() = TokenDTO(
        login = this[Tokens.login],
        accessToken = this[Tokens.accessToken],
        refreshToken = this[Tokens.refreshToken]
    )

    suspend fun createAndSaveTokens(login: String): TokenState {
        val accessToken = JWTGenerator.makeToken(login)
        val refreshToken = UUID.randomUUID().toString()
        val token = TokenDTO(login, accessToken, refreshToken)
        return if (Tokens.getToken(token.login) != null) {
            if (Tokens.updateRefreshToken(token)) {
                TokenState.Success(token)
            } else {
                TokenState.Error(TokenState.ErrorCodes.ERROR_UPDATE)
            }
        } else {
            if (Tokens.insertToken(token)) {
                TokenState.Success(token)
            } else {
                TokenState.Error(TokenState.ErrorCodes.ERROR_CREATE)
            }
        }
    }

    suspend fun insertToken(token: TokenDTO): Boolean
    suspend fun updateAccessToken(token: TokenDTO): Boolean
    suspend fun updateRefreshToken(token: TokenDTO): Boolean
    suspend fun getToken(login: String): TokenDTO?
    suspend fun getAccessToken(login: String): String?
    suspend fun getRefreshToken(login: String): String?
    suspend fun getRefreshTokenExpiration(login: String): Long?
    suspend fun deleteToken(login: String): Boolean
}