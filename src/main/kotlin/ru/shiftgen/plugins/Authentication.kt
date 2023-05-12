package ru.shiftgen.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import ru.shiftgen.databse.authorization.users.Users
import ru.shiftgen.databse.content.structures.Structures
import java.util.*

fun Application.configureAuthentication() {

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JWTGenerator.verifier)
            realm = JWTGenerator.realm
            validate {
                val expiredTime = it.expiresAt
                if (expiredTime != null && expiredTime > Date()) {
                    val login = it.payload.getClaim("login").asString()
                    if (!login.isNullOrBlank() && runBlocking { Users.ifUserExist(login) }) {
                        val structureId = it.payload.getClaim("structureId").asInt()
                        if (
                            structureId != null
                            && runBlocking { Structures.ifStructureExists(structureId) }
                            && structureId == runBlocking { Users.getUserByLogin(login) }?.structureId
                        ) {
                            JWTPrincipal(it.payload)
                        } else null
                    } else null
                } else null
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "JWT токен неверный или истёк срок его действия.")
            }
        }
    }
}

val ApplicationCall.login: String?
    get() = this.principal<JWTPrincipal>()?.payload?.getClaim("login")?.asString()

val ApplicationCall.structureId: Int?
    get() = this.principal<JWTPrincipal>()?.payload?.getClaim("structureId")?.asInt()
