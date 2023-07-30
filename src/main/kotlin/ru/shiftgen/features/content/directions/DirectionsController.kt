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
        Directions.getDirectionStructureId(receive.id)?.let { directionStructureId ->
            if (directionStructureId == structureId) {
                Directions.getDirection(receive.id)?.let { direction ->
                    respond(DirectionResponse(direction))
                } ?: respond(HttpStatusCode.InternalServerError, "Ошибка получения направления.")
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры")
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
        Directions.getDirectionStructureId(receive.id)?.let { directionStructureId ->
            if (directionStructureId == structureId) {
                if (Directions.updateDirection(DirectionDTO(receive.id, receive.name))) {
                    respond(HttpStatusCode.OK, "Направление обновлено.")
                } else {
                    respond(HttpStatusCode.InternalServerError, "Ошибка обновления направления.")
                }
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры")
    }
}

suspend fun ApplicationCall.deleteDirection() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        Directions.getDirectionStructureId(receive.id)?.let { directionStructureId ->
            if (directionStructureId == structureId) {
                if (Directions.deleteDirection(receive.id)) {
                    respond(HttpStatusCode.OK, "Направление удалено.")
                } else {
                    respond(HttpStatusCode.InternalServerError, "Ошибка удаления направления.")
                }
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры")
    }
}
