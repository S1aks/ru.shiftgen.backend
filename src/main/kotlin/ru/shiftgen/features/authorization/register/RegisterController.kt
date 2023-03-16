package ru.shiftgen.features.authorization.register

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.authorization.groups.Groups
import ru.shiftgen.databse.authorization.tokens.TokenDTO
import ru.shiftgen.databse.authorization.tokens.Tokens
import ru.shiftgen.databse.authorization.users.UserDTO
import ru.shiftgen.databse.authorization.users.Users
import ru.shiftgen.plugins.JWTGenerator
import ru.shiftgen.utils.isValidEmail
import java.util.*

class RegisterController(private val call: ApplicationCall) {

    suspend fun registerNewUser() {
        val receive = call.receive<RegisterReceive>()
        if (!receive.email.isValidEmail()) {
            call.respond(HttpStatusCode.BadRequest, "Email is not valid")
            return
        }
        val userId = Users.getUserId(receive.login)
        if (userId != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return
        }
        val user: UserDTO?
        when (receive.group) {
            Groups.DISPATCHER.ordinal -> {
                user = UserDTO(
                    id = 0,
                    login = receive.login,
                    password = receive.password,
                    email = receive.email,
                    phone = "",
                    firstName = "",
                    lastName = "",
                    patronymic = "",
                    accessGroup = Groups.DISPATCHER,
                    structureId = receive.structureId
                )
            }

            Groups.WORKER.ordinal -> {
                user = UserDTO(
                    id = 0,
                    login = receive.login,
                    password = receive.password,
                    email = receive.email,
                    phone = "",
                    firstName = "",
                    lastName = "",
                    patronymic = "",
                    accessGroup = Groups.WORKER,
                    structureId = receive.structureId
                )
            }

            else -> {
                call.respond(HttpStatusCode.Conflict, "Group not exists")
                return
            }
        }
        if (Users.insertUser(user)) {
            val accessToken = JWTGenerator.makeToken(user.login, user.structureId)
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