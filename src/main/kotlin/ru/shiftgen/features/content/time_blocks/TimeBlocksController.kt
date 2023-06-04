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
    structureId?.let { structureId ->
        val list = TimeBlocks.getTimeBlocks(structureId)
        if (list.isNotEmpty()) {
            respond(TimeBlocksResponse(list))
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка получения временных блоков.")
        }
    }
}

suspend fun ApplicationCall.getTimeBlock() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        TimeBlocks.getTimeBlock(receive.id)?.let { timeBlock ->
            if (timeBlock.structureId == structureId) {
                respond(TimeBlockResponse(timeBlock))
            } else {
                respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } ?: respond(HttpStatusCode.InternalServerError, "Ошибка получения временного блока.")
    }
}

suspend fun ApplicationCall.insertTimeBlock() {
    structureId?.let { structureId ->
        val receive = receive<TimeBlockReceive>()
        if (TimeBlocks.insertTimeBlock(
                TimeBlockDTO(0, structureId, receive.name, receive.duration, receive.action)
            )
        ) {
            respond(HttpStatusCode.OK, "Временной блок добавлен.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка добавления временного блока.")
        }
    }
}

suspend fun ApplicationCall.updateTimeBlock() {
    structureId?.let { structureId ->
        val receive = receive<TimeBlockReceive>()
        if (TimeBlocks.updateTimeBlock(
                TimeBlockDTO(receive.id, structureId, receive.name, receive.duration, receive.action)
            )
        ) {
            respond(HttpStatusCode.OK, "Временной блок обновлен.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка обновления временного блока.")
        }
    }
}

suspend fun ApplicationCall.deleteTimeBlock() {
    structureId?.let {
        val receive = receive<IdReceive>()
        if (TimeBlocks.deleteTimeBlock(receive.id)) {
            respond(HttpStatusCode.OK, "Временной блок удален.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка удаления временного блока.")
        }
    }
}