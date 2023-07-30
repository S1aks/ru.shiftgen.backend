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
            respond(HttpStatusCode.InternalServerError, "Список смен пуст.")
        }
    }
}

suspend fun ApplicationCall.getShift() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        Shifts.getShiftStructureId(receive.id)?.let { shiftStructureId ->
            if (shiftStructureId == structureId) {
                Shifts.getShift(receive.id)?.let { shift ->
                    respond(ShiftResponse(shift))
                } ?: respond(HttpStatusCode.InternalServerError, "Ошибка получения смены.")
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}

suspend fun ApplicationCall.insertShift() {
    structureId?.let { structureId ->
        val receive = receive<ShiftReceive>()
        if (Shifts.insertShift(
                structureId,
                ShiftDTO(
                    0,
                    receive.name,
                    receive.periodicity,
                    receive.workerId,
                    receive.directionId,
                    receive.action,
                    receive.startTime,
                    receive.duration,
                    receive.restDuration
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
        Shifts.getShiftStructureId(receive.id)?.let { shiftStructureId ->
            if (shiftStructureId == structureId) {
                if (Shifts.updateShift(
                        ShiftDTO(
                            receive.id,
                            receive.name,
                            receive.periodicity,
                            receive.workerId,
                            receive.directionId,
                            receive.action,
                            receive.startTime,
                            receive.duration,
                            receive.restDuration
                        )
                    )
                ) {
                    respond(HttpStatusCode.OK, "Смена обновлена.")
                } else {
                    respond(HttpStatusCode.InternalServerError, "Ошибка обновления смены.")
                }
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}

suspend fun ApplicationCall.deleteShift() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        Shifts.getShiftStructureId(receive.id)?.let { shiftStructureId ->
            if (shiftStructureId == structureId) {
                if (Shifts.deleteShift(receive.id)) {
                    respond(HttpStatusCode.OK, "Смена удалена.")
                } else {
                    respond(HttpStatusCode.InternalServerError, "Ошибка удаления смены.")
                }
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}

suspend fun ApplicationCall.getYearMonths() {
    structureId?.let { structureId ->
        val yearMonths = Shifts.getYearMonths(structureId)
        if (yearMonths.isNotEmpty()) {
            respond(YearMonthsResponse(yearMonths))
        } else {
            respond(HttpStatusCode.InternalServerError, "Список смен пуст.")
        }
    }
}
