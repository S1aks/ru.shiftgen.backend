package ru.shiftgen.features.authorization.login

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.authorization.tokens.TokenState
import ru.shiftgen.databse.authorization.tokens.Tokens
import ru.shiftgen.databse.authorization.users.Users

suspend fun ApplicationCall.performLogin() {
    val receive = this.receive<LoginRequest>()
    val user = Users.getUserByLogin(receive.login)
    if (user == null) {
        this.respond(HttpStatusCode.BadRequest, "User not found")
        return
    }
    if (user.password == receive.password && user.structureId != null) {
        when (val tokenState = Tokens.createAndSaveTokens(receive.login, user.structureId)) {
            is TokenState.Success -> {
                this.respond(LoginResponse(tokenState.data.accessToken, tokenState.data.refreshToken))
            }

            is TokenState.Error -> {
                when (tokenState.statusCode) {
                    TokenState.ErrorCodes.ERROR_CREATE ->
                        this.respond(HttpStatusCode.InternalServerError, "Error create token")

                    TokenState.ErrorCodes.ERROR_UPDATE ->
                        this.respond(HttpStatusCode.InternalServerError, "Error update token")
                }
            }
        }
    } else {
        this.respond(HttpStatusCode.BadRequest, "Invalid password")
    }
}

suspend fun ApplicationCall.refreshToken() {
    val receive = this.receive<RefreshRequest>()
    val user = Users.getUserByLogin(receive.login)
    val token = Tokens.getRefreshToken(receive.login)
    if (user == null) {
        this.respond(HttpStatusCode.BadRequest, "User not found")
        return
    }
    if (token == receive.refreshToken && user.structureId != null) {
        when (val tokenState = Tokens.createAndSaveTokens(receive.login, user.structureId)) {
            is TokenState.Success -> {
                this.respond(LoginResponse(tokenState.data.accessToken, tokenState.data.refreshToken))
            }

            is TokenState.Error -> {
                when (tokenState.statusCode) {
                    TokenState.ErrorCodes.ERROR_CREATE ->
                        this.respond(HttpStatusCode.InternalServerError, "Error create token")

                    TokenState.ErrorCodes.ERROR_UPDATE ->
                        this.respond(HttpStatusCode.InternalServerError, "Error update token")
                }
            }
        }
    } else {
        this.respond(HttpStatusCode.BadRequest, "Invalid refresh token, please login again!")
    }
}