package ru.shiftgen.features.content

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import ru.shiftgen.features.content.directions.DirectionsController

fun Application.configureContentRouting() {

    routing {
        authenticate("auth-jwt") {

            get("/directions") {
                DirectionsController(call).getAllDirections()
            }

            post("/direction") {
                DirectionsController(call).insertDirection()
            }
        }
    }
}
