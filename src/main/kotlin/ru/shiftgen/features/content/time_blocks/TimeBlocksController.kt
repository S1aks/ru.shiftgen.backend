package ru.shiftgen.features.content.time_blocks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.time_blocks.TimeBlockDTO
import ru.shiftgen.databse.content.time_blocks.TimeBlocks
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

class TimeBlocksController(private val call: ApplicationCall) {
    suspend fun getTimeBlocks() {
        call.structureId?.let { structureId ->
            val list = TimeBlocks.getTimeBlocks(structureId)
            if (list.isNotEmpty()) {
                call.respond(TimeBlocksResponse(list))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting time_blocks data")
            }
        }
    }

    suspend fun getTimeBlock() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            TimeBlocks.getTimeBlock(receive.id)?.let { timeBlock ->
                if (timeBlock.structureId == structureId) {
                    call.respond(TimeBlockResponse(timeBlock))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Error in data structure id")
                }
            } ?: call.respond(HttpStatusCode.InternalServerError, "Error getting time_block data")
        }
    }

    suspend fun insertTimeBlock() {
        call.structureId?.let { structureId ->
            val receive = call.receive<TimeBlockReceive>()
            if (!TimeBlocks.insertTimeBlock(
                    TimeBlockDTO(0, structureId, receive.name, receive.duration, receive.action)
                )
            ) {
                call.respond(HttpStatusCode.InternalServerError, "Error insert time_block data")
            }
        }
    }

    suspend fun updateTimeBlock() {
        call.structureId?.let { structureId ->
            val receive = call.receive<TimeBlockReceive>()
            if (!TimeBlocks.updateTimeBlock(
                    TimeBlockDTO(receive.id, structureId, receive.name, receive.duration, receive.action)
                )
            ) {
                call.respond(HttpStatusCode.InternalServerError, "Error update time_block data")
            }
        }
    }

    suspend fun deleteTimeBlock() {
        call.structureId?.let {
            val receive = call.receive<IdReceive>()
            if (!TimeBlocks.deleteTimeBlock(receive.id)) {
                call.respond(HttpStatusCode.InternalServerError, "Error delete time_block data")
            }
        }
    }
}