package ru.shiftgen.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.shiftgen.databse.content.structures.Structures

fun Application.configureAuthentication() {

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JWTGenerator.verifier)
            realm = JWTGenerator.realm
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

val ApplicationCall.structureId: Int?
    get() {
        val id = this.principal<JWTPrincipal>()?.payload?.getClaim("structureId")?.asInt()
        return if (id != null && runBlocking { Structures.ifStructureExists(id) }) {
            id
        } else {
            runBlocking { this@structureId.respond(HttpStatusCode.BadRequest, "Error in structure id query parameter") }
            null
        }

    }
