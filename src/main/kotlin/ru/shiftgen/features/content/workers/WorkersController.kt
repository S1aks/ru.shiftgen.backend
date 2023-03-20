package ru.shiftgen.features.content.workers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.workers.WorkerDTO
import ru.shiftgen.databse.content.workers.Workers
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

class WorkersController(private val call: ApplicationCall) {
    suspend fun getWorkers() {
        call.structureId?.let { structureId ->
            val list = Workers.getWorkers(structureId)
            if (list.isNotEmpty()) {
                call.respond(WorkersResponse(list))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting workers data")
            }
        }
    }

    suspend fun getWorker() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            Workers.getWorker(receive.id)?.let { worker ->
                if (worker.structureId == structureId) {
                    call.respond(WorkerResponse(worker))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Error in data structure id")
                }
            } ?: call.respond(HttpStatusCode.InternalServerError, "Error getting worker data")
        }
    }

    suspend fun insertWorker() {
        call.structureId?.let { structureId ->
            val receive = call.receive<WorkerReceive>()
            if (!Workers.insertWorker(
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
                call.respond(HttpStatusCode.InternalServerError, "Error insert worker data")
            }
        }
    }

    suspend fun updateWorker() {
        call.structureId?.let { structureId ->
            val receive = call.receive<WorkerReceive>()
            if (!Workers.updateWorker(
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
                call.respond(HttpStatusCode.InternalServerError, "Error update worker data")
            }
        }
    }

    suspend fun deleteWorker() {
        call.structureId?.let {
            val receive = call.receive<IdReceive>()
            if (!Workers.deleteWorker(receive.id)) {
                call.respond(HttpStatusCode.InternalServerError, "Error delete worker data")
            }
        }
    }
}