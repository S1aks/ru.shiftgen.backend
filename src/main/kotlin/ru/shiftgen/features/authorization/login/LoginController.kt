package ru.shiftgen.features.authorization.login

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.authorization.tokens.TokenState
import ru.shiftgen.databse.authorization.tokens.Tokens
import ru.shiftgen.databse.authorization.users.Users

class LoginController(private val call: ApplicationCall) {

    suspend fun performLogin() {
        val receive = call.receive<LoginReceive>()
        val user = Users.getUserByLogin(receive.login)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
            return
        }
        if (user.password == receive.password) {
            when (val tokenState = Tokens.createAndSaveTokens(receive.login)) {
                is TokenState.Success -> {
                    call.respond(LoginResponse(tokenState.data.accessToken, tokenState.data.refreshToken))
                }
                is TokenState.Error -> {
                    when (tokenState.statusCode) {
                        TokenState.ErrorCodes.ERROR_CREATE ->
                            call.respond(HttpStatusCode.InternalServerError, "Error create token")
                        TokenState.ErrorCodes.ERROR_UPDATE ->
                            call.respond(HttpStatusCode.InternalServerError, "Error update token")
                    }
                }
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid password")
        }
    }

    suspend fun refreshToken() {
        val receive = call.receive<RefreshReceive>()
        val user = Users.getUserByLogin(receive.login)
        val token = Tokens.getRefreshToken(receive.login)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
            return
        }
        if (token == receive.refreshToken) {
            when (val tokenState = Tokens.createAndSaveTokens(receive.login)) {
                is TokenState.Success -> {
                    call.respond(LoginResponse(tokenState.data.accessToken, tokenState.data.refreshToken))
                }
                is TokenState.Error -> {
                    when (tokenState.statusCode) {
                        TokenState.ErrorCodes.ERROR_CREATE ->
                            call.respond(HttpStatusCode.InternalServerError, "Error create token")
                        TokenState.ErrorCodes.ERROR_UPDATE ->
                            call.respond(HttpStatusCode.InternalServerError, "Error update token")
                    }
                }
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid refresh token, please login again!")
        }
    }
}