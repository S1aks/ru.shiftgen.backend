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
        this.respond(StructuresResponse(list.associateBy({ it.name }, { it.id })))
    } else {
        this.respond(HttpStatusCode.InternalServerError, "Ошибка получения структур.")
    }
}

suspend fun ApplicationCall.getUserStructureId() {
    this.structureId?.let { structureId ->
        respond(StructureIdResponse(structureId))
    } ?: this.respond(HttpStatusCode.InternalServerError, "ошибка получения id структуры.")
}

suspend fun ApplicationCall.getStructure() {
    this.structureId?.let { structureId ->
        val receive = this.receive<IdReceive>()
        if (receive.id == structureId) {
            Structures.getStructure(receive.id)?.let { structure ->
                this.respond(StructureResponse(structure))
            } ?: this.respond(HttpStatusCode.InternalServerError, "ошибка получения структуры.")
        } else {
            this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        }
    }
}

suspend fun ApplicationCall.insertStructure() {
    val receive = this.receive<StructureReceive>()
    if (Structures.insertStructure(
            StructureDTO(
                0,
                receive.name,
                receive.description,
                receive.restHours,
                receive.allowedConsecutiveNights,
                receive.nightStartHour,
                receive.nightEndHour
            )
        )
    ) {
        this.respond(HttpStatusCode.OK, "Структура добавлена.")
    } else {
        this.respond(HttpStatusCode.InternalServerError, "Ошибка добавления структуры.")
    }
}

suspend fun ApplicationCall.updateStructure() {
    this.structureId?.let { structureId ->
        val receive = this.receive<StructureReceive>()
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
                this.respond(HttpStatusCode.OK, "Структура обновлена.")
            } else {
                this.respond(HttpStatusCode.InternalServerError, "Ошибка обновления структуры.")
            }
        } else {
            this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        }
    }
}

suspend fun ApplicationCall.deleteStructure() {
    this.structureId?.let { structureId ->
        val receive = this.receive<IdReceive>()
        if (receive.id == structureId) {
            if (Structures.deleteStructure(receive.id)) {
                this.respond(HttpStatusCode.OK, "Структура удалена.")
            } else {
                this.respond(HttpStatusCode.InternalServerError, "Ошибка удаления структуры.")
            }
        } else {
            this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        }
    }
}