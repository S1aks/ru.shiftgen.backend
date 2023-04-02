package ru.shiftgen.features.content.time_blocks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.time_blocks.TimeBlockDTO
import ru.shiftgen.databse.content.time_blocks.TimeBlocks
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

suspend fun ApplicationCall.getTimeBlocks() {
    this.structureId?.let { structureId ->
        val list = TimeBlocks.getTimeBlocks(structureId)
        if (list.isNotEmpty()) {
            this.respond(TimeBlocksResponse(list))
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error getting time_blocks data")
        }
    }
}

suspend fun ApplicationCall.getTimeBlock() {
    this.structureId?.let { structureId ->
        val receive = this.receive<IdReceive>()
        TimeBlocks.getTimeBlock(receive.id)?.let { timeBlock ->
            if (timeBlock.structureId == structureId) {
                this.respond(TimeBlockResponse(timeBlock))
            } else {
                this.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        } ?: this.respond(HttpStatusCode.InternalServerError, "Error getting time_block data")
    }
}

suspend fun ApplicationCall.insertTimeBlock() {
    this.structureId?.let { structureId ->
        val receive = this.receive<TimeBlockReceive>()
        if (TimeBlocks.insertTimeBlock(
                TimeBlockDTO(0, structureId, receive.name, receive.duration, receive.action)
            )
        ) {
            this.respond(HttpStatusCode.OK, "TimeBlock data inserted")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error insert time_block data")
        }
    }
}

suspend fun ApplicationCall.updateTimeBlock() {
    this.structureId?.let { structureId ->
        val receive = this.receive<TimeBlockReceive>()
        if (TimeBlocks.updateTimeBlock(
                TimeBlockDTO(receive.id, structureId, receive.name, receive.duration, receive.action)
            )
        ) {
            this.respond(HttpStatusCode.OK, "TimeBlock data updated")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error update time_block data")
        }
    }
}

suspend fun ApplicationCall.deleteTimeBlock() {
    this.structureId?.let {
        val receive = this.receive<IdReceive>()
        if (TimeBlocks.deleteTimeBlock(receive.id)) {
            this.respond(HttpStatusCode.OK, "TimeBlock data deleted")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error delete time_block data")
        }
    }
}