package ru.shiftgen.features.content.directions

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.directions.DirectionDTO
import ru.shiftgen.databse.content.directions.Directions
import ru.shiftgen.plugins.structureId

class DirectionsController(private val call: ApplicationCall) {

    suspend fun getAllDirections() {
        call.structureId?.let { structureId ->
            call.respond(DirectionsResponse(Directions.getAllDirections(structureId)))
        }
    }

    suspend fun insertDirection() {
        call.structureId?.let { structureId ->
            val receive = call.receive<DirectionReceive>()
            Directions.insertDirection(DirectionDTO(receive.id, receive.name, structureId))
        }
    }

    suspend fun updateDirection() {
        call.structureId?.let { structureId ->
            val receive = call.receive<DirectionReceive>()
            Directions.updateDirection(DirectionDTO(receive.id, receive.name, structureId))
        }
    }

    suspend fun deleteDirection() {
        call.structureId?.let {
            val receive = call.receive<DirectionReceive>()
            Directions.deleteDirection(receive.id)
        }
    }
}