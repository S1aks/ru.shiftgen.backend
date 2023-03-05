package ru.shiftgen.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(GWTGenerator.verifier)
            realm = GWTGenerator.realm
            validate {
                if (it.payload.getClaim("login").asString() != "") {
                    JWTPrincipal(it.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}