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
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.plugins.JWTGenerator
import ru.shiftgen.utils.isValidEmail
import java.util.*

suspend fun ApplicationCall.registerNewUser() {
    val receive = this.receive<RegisterRequest>()
    if (!receive.email.isValidEmail()) {
        this.respond(HttpStatusCode.BadRequest, "Email is not valid")
        return
    }
    val userId = Users.getUserId(receive.login)
    if (userId != null) {
        this.respond(HttpStatusCode.Conflict, "User already exists")
        return
    }
    val structure = Structures.getStructure(receive.structureId)
    if (structure == null) {
        this.respond(HttpStatusCode.BadRequest, "Structure not exists")
        return
    }
    val user: UserDTO?
    when (receive.group) {
        Groups.DISPATCHER.ordinal -> {
            user = UserDTO(
                id = 0,
                login = receive.login,
                email = receive.email,
                password = receive.password,
                phone = "",
                firstName = "",
                lastName = "",
                patronymic = "",
                group = Groups.DISPATCHER,
                structureId = receive.structureId
            )
        }

        Groups.WORKER.ordinal -> {
            user = UserDTO(
                id = 0,
                login = receive.login,
                email = receive.email,
                password = receive.password,
                phone = "",
                firstName = "",
                lastName = "",
                patronymic = "",
                group = Groups.WORKER,
                structureId = receive.structureId
            )
        }

        else -> {
            this.respond(HttpStatusCode.Conflict, "Group not exists")
            return
        }
    }
    if (Users.insertUser(user)) {
        val accessToken = JWTGenerator.makeToken(user.login, user.structureId)
        val refreshToken = UUID.randomUUID().toString()
        val token = TokenDTO(user.login, accessToken, refreshToken)
        if (Tokens.insertToken(token)) {
            this.respond(RegisterResponse(token.accessToken, token.refreshToken))
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error create token")
        }
    } else {
        this.respond(HttpStatusCode.InternalServerError, "Error create user")
    }
}