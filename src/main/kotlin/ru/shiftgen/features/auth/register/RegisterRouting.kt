package ru.shiftgen.features.auth.register

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRegisterRouting() {

    routing {
        post("auth/register") {
            RegisterController(call).registerNewUser()
        }
    }
}