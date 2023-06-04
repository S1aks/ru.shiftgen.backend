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
import ru.shiftgen.plugins.JWTService
import ru.shiftgen.utils.isValidEmail
import java.util.*

suspend fun ApplicationCall.registerNewUser() {
    val receive = receive<RegisterReceive>()
    if (!receive.email.isValidEmail()) {
        respond(HttpStatusCode.BadRequest, "Неверный E-mail.")
        return
    }
    val userId = Users.getUserId(receive.login)
    if (userId != null) {
        respond(HttpStatusCode.Conflict, "Пользователь существует.")
        return
    }
    val structure = Structures.getStructure(receive.structureId)
    if (structure == null) {
        respond(HttpStatusCode.BadRequest, "Структура не существует.")
        return
    }
    val dispatcherPin = structure.dispatcherPin
    val structureIsNotEmpty = Users.getUsersByStructure(structure.id).isNotEmpty()
    if (structureIsNotEmpty && dispatcherPin != receive.dispatcherPin) {
        respond(HttpStatusCode.BadRequest, "Pin диспетчера не совпадает.")
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
                structureId = receive.structureId,
            )
        }

        Groups.WORKER.ordinal -> {
            respond(HttpStatusCode.Conflict, "Рабочий пока не может быть создан.")
            return
//            user = UserDTO(
//                id = 0,
//                login = receive.login,
//                email = receive.email,
//                password = receive.password,
//                phone = "",
//                firstName = "",
//                lastName = "",
//                patronymic = "",
//                group = Groups.WORKER,
//                structureId = receive.structureId
//            )
        }

        else -> {
            respond(HttpStatusCode.Conflict, "Группа не существует.")
            return
        }
    }
    if (Users.insertUser(user)) {
        val accessToken = JWTService.makeToken(user.login, user.structureId)
        val refreshToken = UUID.randomUUID().toString()
        val token = TokenDTO(user.login, accessToken, refreshToken)
        if (Tokens.insertToken(token)) {
            respond(RegisterResponse(token.accessToken, token.refreshToken))
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка создания JWT токена.")
        }
    } else {
        respond(HttpStatusCode.InternalServerError, "Ошибка создания пользователя.")
    }
}
