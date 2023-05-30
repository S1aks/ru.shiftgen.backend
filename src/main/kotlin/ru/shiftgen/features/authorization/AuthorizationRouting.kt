package ru.shiftgen.features.authorization

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import ru.shiftgen.features.authorization.login.checkAccess
import ru.shiftgen.features.authorization.login.performLogin
import ru.shiftgen.features.authorization.login.refreshToken
import ru.shiftgen.features.authorization.register.registerNewUser

fun Application.configureAuthorizationRouting() {

    routing {
        post("auth/register") { call.registerNewUser() }
        post("auth/login") { call.performLogin() }
        post("auth/refresh") { call.refreshToken() }
        authenticate("auth-jwt") {
            get("auth/access") { call.checkAccess() }
        }
    }
}