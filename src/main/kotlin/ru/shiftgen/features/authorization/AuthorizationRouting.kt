package ru.shiftgen.features.authorization

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.shiftgen.features.authorization.login.LoginController
import ru.shiftgen.features.authorization.register.RegisterController

fun Application.configureAuthorizationRouting() {

    routing {
        post("auth/register") {
            RegisterController(call).registerNewUser()
        }

        post("auth/login") {
            LoginController(call).performLogin()
        }

        post("auth/refresh") {
            LoginController(call).refreshToken()
        }
    }
}