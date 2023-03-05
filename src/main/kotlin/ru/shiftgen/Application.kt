package ru.shiftgen

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiftgen.databse.access.tokens.Tokens
import ru.shiftgen.databse.access.users.Users
import ru.shiftgen.features.auth.login.configureLoginRouting
import ru.shiftgen.features.auth.register.configureRegisterRouting
import ru.shiftgen.plugins.DatabaseFactory
import ru.shiftgen.plugins.configureAuthentication
import ru.shiftgen.plugins.configureRouting
import ru.shiftgen.plugins.configureSerialization

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureAuthentication()
    configureRouting()
    configureRegisterRouting()
    configureLoginRouting()
}
