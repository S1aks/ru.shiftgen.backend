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
    structureId?.let { structureId ->
        val receive = receive<ShiftsReceive>()
        val list = Shifts.getShifts(structureId, receive.yearMonth)
        if (list.isNotEmpty()) {
            respond(ShiftsResponse(list))
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка получения смен.")
        }
    }
}

suspend fun ApplicationCall.getShift() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        Shifts.getShift(receive.id)?.let { shift ->
            if (shift.structureId == structureId) {
                respond(ShiftResponse(shift))
            } else {
                respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } ?: respond(HttpStatusCode.InternalServerError, "Ошибка получения смены.")
    }
}

suspend fun ApplicationCall.insertShift() {
    structureId?.let { structureId ->
        val receive = receive<ShiftReceive>()
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
            respond(HttpStatusCode.OK, "Смена добавлена.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка добавления смены.")
        }
    }
}

suspend fun ApplicationCall.updateShift() {
    structureId?.let { structureId ->
        val receive = receive<ShiftReceive>()
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
            respond(HttpStatusCode.OK, "Смена обновлена.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка обновления смены.")
        }
    }
}

suspend fun ApplicationCall.deleteShift() {
    structureId?.let {
        val receive = receive<IdReceive>()
        if (Shifts.deleteShift(receive.id)) {
            respond(HttpStatusCode.OK, "Смена удалена.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка удаления смены.")
        }
    }
}
