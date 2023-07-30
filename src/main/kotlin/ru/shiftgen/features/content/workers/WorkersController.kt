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
    structureId?.let { structureId ->
        val list = Workers.getWorkers(structureId)
        if (list.isNotEmpty()) {
            respond(WorkersResponse(list))
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка получения работников.")
        }
    }
}

suspend fun ApplicationCall.getWorker() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        Workers.getWorkerStructureId(receive.id)?.let { workerStructureId ->
            if (workerStructureId == structureId) {
                Workers.getWorker(receive.id)?.let { worker ->
                    respond(WorkerResponse(worker))
                } ?: respond(HttpStatusCode.InternalServerError, "Ошибка получения работника.")
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}

suspend fun ApplicationCall.insertWorker() {
    structureId?.let { structureId ->
        val receive = receive<WorkerReceive>()
        if (Workers.insertWorker(
                structureId,
                WorkerDTO(
                    0,
                    receive.personnelNumber,
                    receive.userId,
                    receive.firstName,
                    receive.lastName,
                    receive.patronymic,
                    receive.accessToDirections
                )
            )
        ) {
            respond(HttpStatusCode.OK, "Работник добавлен.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка добавления работника.")
        }
    }
}

suspend fun ApplicationCall.updateWorker() {
    structureId?.let { structureId ->
        val receive = receive<WorkerReceive>()
        Workers.getWorkerStructureId(receive.id)?.let { workerStructureId ->
            if (workerStructureId == structureId) {
                if (Workers.updateWorker(
                        WorkerDTO(
                            receive.id,
                            receive.personnelNumber,
                            receive.userId,
                            receive.firstName,
                            receive.lastName,
                            receive.patronymic,
                            receive.accessToDirections
                        )
                    )
                ) {
                    respond(HttpStatusCode.OK, "Работник обновлен.")
                } else {
                    respond(HttpStatusCode.InternalServerError, "Ошибка обновления работника.")
                }
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}

suspend fun ApplicationCall.deleteWorker() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        Workers.getWorkerStructureId(receive.id)?.let { workerStructureId ->
            if (workerStructureId == structureId) {
                if (Workers.deleteWorker(receive.id)) {
                    respond(HttpStatusCode.OK, "Работник удален.")
                } else {
                    respond(HttpStatusCode.InternalServerError, "Ошибка удаления работника.")
                }
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}
