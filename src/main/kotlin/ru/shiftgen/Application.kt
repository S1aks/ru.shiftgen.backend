package ru.shiftgen

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import ru.shiftgen.features.authorization.configureAuthorizationRouting
import ru.shiftgen.features.content.configureContentRouting
import ru.shiftgen.plugins.DatabaseFactory
import ru.shiftgen.plugins.configureAuthentication
import ru.shiftgen.plugins.configureSerialization

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureAuthentication()
    configureAuthorizationRouting()
    configureContentRouting()
}
