package ru.shiftgen.features.content.structures

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.structures.StructureDTO
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

suspend fun ApplicationCall.getStructures() {
    val list = Structures.getStructures()
    if (list.isNotEmpty()) {
        respond(StructuresResponse(list.associateBy({ it.name }, { it.id })))
    } else {
        respond(HttpStatusCode.InternalServerError, "Ошибка получения структур.")
    }
}

suspend fun ApplicationCall.getUserStructureId() {
    structureId?.let { structureId ->
        respond(StructureIdResponse(structureId))
    } ?: respond(HttpStatusCode.InternalServerError, "ошибка получения id структуры.")
}

suspend fun ApplicationCall.getStructure() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        if (receive.id == structureId) {
            Structures.getStructure(receive.id)?.let { structure ->
                respond(StructureResponse(structure))
            } ?: respond(HttpStatusCode.InternalServerError, "ошибка получения структуры.")
        } else {
            respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        }
    }
}

private fun generatePin(): String = StringBuilder("").apply {
    repeat(5) { append((Math.random() * 10).toInt()) }
}.toString()

suspend fun ApplicationCall.insertStructure() {
    val receive = receive<StructureReceive>()
    if (Structures.insertStructure(
            StructureDTO(
                0,
                receive.name,
                receive.description,
                receive.restHours,
                receive.allowedConsecutiveNights,
                receive.nightStartHour,
                receive.nightEndHour,
                generatePin()
            )
        )
    ) {
        respond(HttpStatusCode.OK, "Структура добавлена.")
    } else {
        respond(HttpStatusCode.InternalServerError, "Ошибка добавления структуры.")
    }
}

suspend fun ApplicationCall.updateStructure() {
    structureId?.let { structureId ->
        val receive = receive<StructureReceive>()
        if (receive.id == structureId) {
            if (Structures.updateStructure(
                    StructureDTO(
                        receive.id,
                        receive.name,
                        receive.description,
                        receive.restHours,
                        receive.allowedConsecutiveNights,
                        receive.nightStartHour,
                        receive.nightEndHour
                    )
                )
            ) {
                respond(HttpStatusCode.OK, "Структура обновлена.")
            } else {
                respond(HttpStatusCode.InternalServerError, "Ошибка обновления структуры.")
            }
        } else {
            respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        }
    }
}

suspend fun ApplicationCall.deleteStructure() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        if (receive.id == structureId) {
            if (Structures.deleteStructure(receive.id)) {
                respond(HttpStatusCode.OK, "Структура удалена.")
            } else {
                respond(HttpStatusCode.InternalServerError, "Ошибка удаления структуры.")
            }
        } else {
            respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        }
    }
}
