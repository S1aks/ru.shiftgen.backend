package ru.shiftgen.features.content.structures

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.structures.StructureDTO
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

class StructuresController(private val call: ApplicationCall) {
    suspend fun getStructures() {
        call.structureId?.let { _ ->
            val list = Structures.getStructures()
            if (list.isNotEmpty()) {
                call.respond(StructuresResponse(list.associateBy({ it.id }, { it.name })))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting structures data")
            }
        }
    }

    suspend fun getStructure() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            if (receive.id == structureId) {
                Structures.getStructure(receive.id)?.let { structure ->
                    call.respond(StructureResponse(structure))
                } ?: call.respond(HttpStatusCode.InternalServerError, "Error getting structure data")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        }
    }

    suspend fun insertStructure() {
        val receive = call.receive<StructureReceive>()
        if (!Structures.insertStructure(
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
            call.respond(HttpStatusCode.InternalServerError, "Error insert structure data")
        }
    }

    suspend fun updateStructure() {
        call.structureId?.let { structureId ->
            val receive = call.receive<StructureReceive>()
            if (receive.id == structureId) {
                if (!Structures.updateStructure(
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
                    call.respond(HttpStatusCode.InternalServerError, "Error update structure data")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        }
    }

    suspend fun deleteStructure() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            if (receive.id == structureId) {
                if (!Structures.deleteStructure(receive.id)) {
                    call.respond(HttpStatusCode.InternalServerError, "Error delete structure data")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        }
    }
}