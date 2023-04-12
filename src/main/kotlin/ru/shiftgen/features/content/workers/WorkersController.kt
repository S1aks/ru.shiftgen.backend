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
            this.respond(HttpStatusCode.InternalServerError, "Error getting workers data")
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
                this.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        } ?: this.respond(HttpStatusCode.InternalServerError, "Error getting worker data")
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
            this.respond(HttpStatusCode.OK, "Worker data inserted")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error insert worker data")
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
            this.respond(HttpStatusCode.OK, "Worker data updated")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error update worker data")
        }
    }
}

suspend fun ApplicationCall.deleteWorker() {
    this.structureId?.let {
        val receive = this.receive<IdReceive>()
        if (Workers.deleteWorker(receive.id)) {
            this.respond(HttpStatusCode.OK, "Worker data deleted")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error delete worker data")
        }
    }
}