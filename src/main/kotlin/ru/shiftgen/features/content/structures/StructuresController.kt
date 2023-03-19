package ru.shiftgen.features.content.structures

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.structures.StructureDTO
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.features.content.IdReceive

class StructuresController(private val call: ApplicationCall) {
    suspend fun getStructures() {
        val list = Structures.getStructures()
        if (list.isNotEmpty()) {
            call.respond(StructuresResponse(list))
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Error getting structures data")
        }
    }

    suspend fun getStructure() {
        val receive = call.receive<IdReceive>()
        Structures.getStructure(receive.id)?.let { structure ->
            call.respond(StructureResponse(structure))
        } ?: call.respond(HttpStatusCode.InternalServerError, "Error getting structure data")
    }

    suspend fun insertStructure() {
        val receive = call.receive<StructureReceive>()
        if (!Structures.insertStructure(StructureDTO(0, receive.name, receive.description))) {
            call.respond(HttpStatusCode.InternalServerError, "Error insert structure data")
        }
    }

    suspend fun updateStructure() {
        val receive = call.receive<StructureReceive>()
        if (!Structures.updateStructure(StructureDTO(receive.id, receive.name, receive.description))) {
            call.respond(HttpStatusCode.InternalServerError, "Error update structure data")
        }
    }

    suspend fun deleteStructure() {
        val receive = call.receive<IdReceive>()
        if (!Structures.deleteStructure(receive.id)) {
            call.respond(HttpStatusCode.InternalServerError, "Error delete structure data")
        }
    }
}