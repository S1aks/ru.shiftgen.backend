package ru.shiftgen.features.auth.register

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.access.tokens.TokenDTO
import ru.shiftgen.databse.access.tokens.Tokens
import ru.shiftgen.databse.access.users.UserDTO
import ru.shiftgen.databse.access.users.Users
import ru.shiftgen.plugins.GWTGenerator
import ru.shiftgen.utils.isValidEmail
import java.util.*

class RegisterController(private val call: ApplicationCall) {

    suspend fun registerNewUser() {
        val receive = call.receive<RegisterReceive>()
        if (!receive.email.isValidEmail()) {
            call.respond(HttpStatusCode.BadRequest, "Email is not valid")
        }
        val userId = Users.getUserId(receive.login)
        if (userId != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
        } else {
            val user = UserDTO(
                id = 0,
                login = receive.login,
                password = receive.password,
                email = receive.email,
                phone = "",
                firstName = "",
                lastName = "",
                patronymic = ""
            )
            if (Users.insertUser(user)) {
                val accessToken = GWTGenerator.makeToken(user.login)
                val refreshToken = UUID.randomUUID().toString()
                val token = TokenDTO(user.login, accessToken, refreshToken)
                if (Tokens.insertToken(token)) {
                    call.respond(RegisterResponse(token.accessToken, token.refreshToken))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Error create token")
                }
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error create user")
            }
        }
    }
}