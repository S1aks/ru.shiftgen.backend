package ru.shiftgen

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import ru.shiftgen.features.authorization.configureAuthorizationRouting
import ru.shiftgen.features.content.configureContentRouting
import ru.shiftgen.plugins.DatabaseFactory
import ru.shiftgen.plugins.configureAuthentication
import ru.shiftgen.plugins.configurePlugins

fun main() {
    embeddedServer(CIO, port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureAuthentication()
    configurePlugins()
    configureAuthorizationRouting()
    configureContentRouting()
}
