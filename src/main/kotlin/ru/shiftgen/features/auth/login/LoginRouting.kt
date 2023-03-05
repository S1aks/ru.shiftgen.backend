package ru.shiftgen.features.auth.login

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLoginRouting() {

    routing {
        post("auth/login") {
            LoginController(call).performLogin()
        }

        post("auth/refresh") {
            LoginController(call).refreshToken()
        }
    }
}