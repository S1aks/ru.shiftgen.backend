package ru.shiftgen.features.content.directions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.directions.DirectionDTO
import ru.shiftgen.databse.content.directions.Directions
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

suspend fun ApplicationCall.getDirections() {
    this.structureId?.let { structureId ->
        val list = Directions.getDirections(structureId)
        if (list.isNotEmpty()) {
            this.respond(DirectionsResponse(list))
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error getting directions data")
        }
    }
}

suspend fun ApplicationCall.getDirection() {
    this.structureId?.let { structureId ->
        val receive = this.receive<IdReceive>()
        Directions.getDirection(receive.id)?.let { direction ->
            if (direction.structureId == structureId) {
                this.respond(DirectionResponse(direction))
            } else {
                this.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        } ?: this.respond(HttpStatusCode.InternalServerError, "Error getting direction data")
    }
}

suspend fun ApplicationCall.insertDirection() {
    this.structureId?.let { structureId ->
        val receive = this.receive<DirectionReceive>()
        if (!Directions.insertDirection(DirectionDTO(0, receive.name, structureId))) {
            this.respond(HttpStatusCode.InternalServerError, "Error insert direction data")
        }
    }
}

suspend fun ApplicationCall.updateDirection() {
    this.structureId?.let { structureId ->
        val receive = this.receive<DirectionReceive>()
        if (!Directions.updateDirection(DirectionDTO(receive.id, receive.name, structureId))) {
            this.respond(HttpStatusCode.InternalServerError, "Error update direction data")
        }
    }
}

suspend fun ApplicationCall.deleteDirection() {
    this.structureId?.let {
        val receive = this.receive<IdReceive>()
        if (!Directions.deleteDirection(receive.id)) {
            this.respond(HttpStatusCode.InternalServerError, "Error delete direction data")
        }
    }
}