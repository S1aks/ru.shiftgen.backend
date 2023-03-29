package ru.shiftgen.features.content.directions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.directions.DirectionDTO
import ru.shiftgen.databse.content.directions.Directions
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

class DirectionsController(private val call: ApplicationCall) {

    suspend fun getDirections() {
        call.structureId?.let { structureId ->
            val list = Directions.getDirections(structureId)
            if (list.isNotEmpty()) {
                call.respond(DirectionsResponse(list))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting directions data")
            }
        }
    }

    suspend fun getDirection() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            Directions.getDirection(receive.id)?.let { direction ->
                if (direction.structureId == structureId) {
                    call.respond(DirectionResponse(direction))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
                }
            } ?: call.respond(HttpStatusCode.InternalServerError, "Error getting direction data")
        }
    }

    suspend fun insertDirection() {
        call.structureId?.let { structureId ->
            val receive = call.receive<DirectionReceive>()
            if (!Directions.insertDirection(DirectionDTO(0, receive.name, structureId))) {
                call.respond(HttpStatusCode.InternalServerError, "Error insert direction data")
            }
        }
    }

    suspend fun updateDirection() {
        call.structureId?.let { structureId ->
            val receive = call.receive<DirectionReceive>()
            if (!Directions.updateDirection(DirectionDTO(receive.id, receive.name, structureId))) {
                call.respond(HttpStatusCode.InternalServerError, "Error update direction data")
            }
        }
    }

    suspend fun deleteDirection() {
        call.structureId?.let {
            val receive = call.receive<IdReceive>()
            if (!Directions.deleteDirection(receive.id)) {
                call.respond(HttpStatusCode.InternalServerError, "Error delete direction data")
            }
        }
    }
}