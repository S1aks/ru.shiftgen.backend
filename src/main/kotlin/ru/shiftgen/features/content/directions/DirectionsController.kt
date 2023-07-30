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
    structureId?.let { structureId ->
        val list = Directions.getDirections(structureId)
        if (list.isNotEmpty()) {
            respond(DirectionsResponse(list))
        } else {
            respond(HttpStatusCode.InternalServerError, "Список направлений пуст.")
        }
    }
}

suspend fun ApplicationCall.getDirection() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        Directions.getDirection(receive.id)?.let { direction ->
            if (Directions.getDirectionStructureId(receive.id) == structureId) {
                respond(DirectionResponse(direction))
            } else {
                respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры")
            }
        } ?: respond(HttpStatusCode.InternalServerError, "Ошибка получения направления.")
    }
}

suspend fun ApplicationCall.insertDirection() {
    structureId?.let { structureId ->
        val receive = receive<DirectionReceive>()
        if (Directions.insertDirection(structureId, DirectionDTO(0, receive.name))) {
            respond(HttpStatusCode.OK, "Направление добавлено.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка добавления направления.")
        }
    }
}

suspend fun ApplicationCall.updateDirection() {
    structureId?.let { structureId ->
        val receive = receive<DirectionReceive>()
        if (Directions.updateDirection(structureId, DirectionDTO(receive.id, receive.name))) {
            respond(HttpStatusCode.OK, "Направление обновлено.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка обновления направления.")
        }
    }
}

suspend fun ApplicationCall.deleteDirection() {
    structureId?.let {
        val receive = receive<IdReceive>()
        if (Directions.deleteDirection(receive.id)) {
            respond(HttpStatusCode.OK, "Направление удалено.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка удаления направления.")
        }
    }
}
