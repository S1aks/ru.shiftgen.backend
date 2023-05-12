package ru.shiftgen.features.content.shifts

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.databse.content.shifts.Shifts
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

suspend fun ApplicationCall.getShifts() {
    this.structureId?.let { structureId ->
        val receive = this.receive<ShiftsRequest>()
        val list = Shifts.getShifts(structureId, receive.yearMonth)
        if (list.isNotEmpty()) {
            this.respond(ShiftsResponse(list))
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка получения смен.")
        }
    }
}

suspend fun ApplicationCall.getShift() {
    this.structureId?.let { structureId ->
        val receive = this.receive<IdReceive>()
        Shifts.getShift(receive.id)?.let { shift ->
            if (shift.structureId == structureId) {
                this.respond(ShiftResponse(shift))
            } else {
                this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } ?: this.respond(HttpStatusCode.InternalServerError, "Ошибка получения смены.")
    }
}

suspend fun ApplicationCall.insertShift() {
    this.structureId?.let { structureId ->
        val receive = this.receive<ShiftRequest>()
        if (Shifts.insertShift(
                ShiftDTO(
                    0,
                    receive.name,
                    receive.yearMonth,
                    receive.periodicity,
                    receive.workerId,
                    structureId,
                    receive.directionId,
                    receive.startTime,
                    receive.timeBlocksIds
                )
            )
        ) {
            this.respond(HttpStatusCode.OK, "Смена добавлена.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка добавления смены.")
        }
    }
}

suspend fun ApplicationCall.updateShift() {
    this.structureId?.let { structureId ->
        val receive = this.receive<ShiftRequest>()
        if (Shifts.updateShift(
                ShiftDTO(
                    receive.id,
                    receive.name,
                    receive.yearMonth,
                    receive.periodicity,
                    receive.workerId,
                    structureId,
                    receive.directionId,
                    receive.startTime,
                    receive.timeBlocksIds
                )
            )
        ) {
            this.respond(HttpStatusCode.OK, "Смена обновлена.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка обновления смены.")
        }
    }
}

suspend fun ApplicationCall.deleteShift() {
    this.structureId?.let {
        val receive = this.receive<IdReceive>()
        if (Shifts.deleteShift(receive.id)) {
            this.respond(HttpStatusCode.OK, "Смена удалена.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка удаления смены.")
        }
    }
}