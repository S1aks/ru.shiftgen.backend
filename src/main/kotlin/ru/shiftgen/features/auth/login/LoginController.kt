package ru.shiftgen.features.auth.login

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.access.tokens.TokenDTO
import ru.shiftgen.databse.access.tokens.Tokens
import ru.shiftgen.databse.access.users.Users
import ru.shiftgen.plugins.GWTGenerator
import java.util.*

class LoginController(private val call: ApplicationCall) {

    suspend fun performLogin() {
        val receive = call.receive<LoginReceive>()
        val userId = Users.getUserId(receive.login)
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
        } else {
            val user = Users.getUserById(userId)
            if (user.password == receive.password) {
                val accessToken = GWTGenerator.makeToken(receive.login)
                val refreshToken = UUID.randomUUID().toString()
                val token = TokenDTO(user.login, accessToken, refreshToken)
                if (Tokens.getToken(token.login) != null) {
                    if (Tokens.updateRefreshToken(token)) {
                        call.respond(LoginResponse(token.accessToken, token.refreshToken))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Error update token")
                    }
                } else {
                    if (Tokens.insertToken(token)) {
                        call.respond(LoginResponse(accessToken, refreshToken))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Error create token")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid password")
            }
        }
    }

    suspend fun refreshToken() {
        val receive = call.receive<RefreshReceive>()
        val userId = Users.getUserId(receive.login)
        val user = Users.getUserById(userId)
        val token = Tokens.getRefreshToken(receive.login)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
        } else {
            if (token == receive.refreshToken) {
                val accessToken = GWTGenerator.makeToken(receive.login)
                val refreshToken = UUID.randomUUID().toString()
                val newToken = TokenDTO(user.login, accessToken, refreshToken)
                if (Tokens.getToken(newToken.login) != null) {
                    if (Tokens.updateRefreshToken(newToken)) {
                        call.respond(LoginResponse(newToken.accessToken, newToken.refreshToken))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Error update token")
                    }
                } else {
                    Tokens.insertToken(newToken)?.let { savedToken ->
                        call.respond(LoginResponse(savedToken.accessToken, savedToken.refreshToken))
                    } ?: call.respond(HttpStatusCode.InternalServerError, "Error create token")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid refresh token, please login again!")
            }
        }
    }
}