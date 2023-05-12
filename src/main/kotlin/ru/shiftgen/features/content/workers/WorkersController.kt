package ru.shiftgen.features.content.workers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.workers.WorkerDTO
import ru.shiftgen.databse.content.workers.Workers
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

suspend fun ApplicationCall.getWorkers() {
    this.structureId?.let { structureId ->
        val list = Workers.getWorkers(structureId)
        if (list.isNotEmpty()) {
            this.respond(WorkersResponse(list))
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка получения работников.")
        }
    }
}

suspend fun ApplicationCall.getWorker() {
    this.structureId?.let { structureId ->
        val receive = this.receive<IdReceive>()
        Workers.getWorker(receive.id)?.let { worker ->
            if (worker.structureId == structureId) {
                this.respond(WorkerResponse(worker))
            } else {
                this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } ?: this.respond(HttpStatusCode.InternalServerError, "Ошибка получения работника.")
    }
}

suspend fun ApplicationCall.insertWorker() {
    this.structureId?.let { structureId ->
        val receive = this.receive<WorkerRequest>()
        if (Workers.insertWorker(
                WorkerDTO(
                    0,
                    receive.personnelNumber,
                    receive.userId,
                    structureId,
                    receive.firstName,
                    receive.lastName,
                    receive.patronymic,
                    receive.accessToDirections
                )
            )
        ) {
            this.respond(HttpStatusCode.OK, "Работник добавлен.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка добавления работника.")
        }
    }
}

suspend fun ApplicationCall.updateWorker() {
    this.structureId?.let { structureId ->
        val receive = this.receive<WorkerRequest>()
        if (Workers.updateWorker(
                WorkerDTO(
                    receive.id,
                    receive.personnelNumber,
                    receive.userId,
                    structureId,
                    receive.firstName,
                    receive.lastName,
                    receive.patronymic,
                    receive.accessToDirections
                )
            )
        ) {
            this.respond(HttpStatusCode.OK, "Работник обновлен.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка обновления работника.")
        }
    }
}

suspend fun ApplicationCall.deleteWorker() {
    this.structureId?.let {
        val receive = this.receive<IdReceive>()
        if (Workers.deleteWorker(receive.id)) {
            this.respond(HttpStatusCode.OK, "Работник удален.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка удаления работника.")
        }
    }
}