package ru.shiftgen.features.authorization.login

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.authorization.tokens.TokenState
import ru.shiftgen.databse.authorization.tokens.Tokens
import ru.shiftgen.databse.authorization.users.Users
import ru.shiftgen.plugins.PasswordEncryptor
import ru.shiftgen.plugins.login
import ru.shiftgen.plugins.structureId

suspend fun ApplicationCall.performLogin() {
    val receive = this.receive<LoginRequest>()
    val user = Users.getUserByLogin(receive.login)
    if (user == null) {
        this.respond(HttpStatusCode.BadRequest, "Пользователь не найден.")
        return
    }
    val encryptedPassword = PasswordEncryptor.hash(receive.password)
    if (user.password == encryptedPassword && user.structureId != null) {
        when (val tokenState = Tokens.createAndSaveTokens(receive.login, user.structureId)) {
            is TokenState.Success -> {
                this.respond(LoginResponse(tokenState.data.accessToken, tokenState.data.refreshToken))
            }

            is TokenState.Error -> {
                when (tokenState.statusCode) {
                    TokenState.ErrorCodes.ERROR_CREATE ->
                        this.respond(HttpStatusCode.InternalServerError, "Ошибка создания токена.")

                    TokenState.ErrorCodes.ERROR_UPDATE ->
                        this.respond(HttpStatusCode.InternalServerError, "Ошибка обновления токена.")
                }
            }
        }
    } else {
        this.respond(HttpStatusCode.BadRequest, "Неверный пароль.")
    }
}

suspend fun ApplicationCall.checkAccess() {
    this.respond(HttpStatusCode.OK, "Успешная авторизация.")
}

suspend fun ApplicationCall.refreshToken() {
    this.login?.let { login ->
        val receive = this.receive<RefreshRequest>()
        val user = Users.getUserByLogin(login)
        val token = Tokens.getRefreshToken(login)
        if (user == null) {
            this.respond(HttpStatusCode.BadRequest, "Пользователь не найден.")
            return
        }
        if (user.structureId != this.structureId) {
            this.respond(HttpStatusCode.BadRequest, "Ошибка в JWT токене: неверный id структуры.")
            return
        }
        if (token == receive.refreshToken && user.structureId != null) {
            when (val tokenState = Tokens.createAndSaveTokens(login, user.structureId)) {
                is TokenState.Success -> {
                    this.respond(LoginResponse(tokenState.data.accessToken, tokenState.data.refreshToken))
                }

                is TokenState.Error -> {
                    when (tokenState.statusCode) {
                        TokenState.ErrorCodes.ERROR_CREATE ->
                            this.respond(HttpStatusCode.InternalServerError, "Ошибка создания токена.")

                        TokenState.ErrorCodes.ERROR_UPDATE ->
                            this.respond(HttpStatusCode.InternalServerError, "Ошибка обновления токена.")
                    }
                }
            }
        } else {
            this.respond(HttpStatusCode.BadRequest, "Неверный refresh токен, залогиньтесь заново!")
        }
    }
}