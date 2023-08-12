package ru.shiftgen.features.content.shifts

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.enums.Periodicity
import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.databse.content.shifts.Shifts
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId
import ru.shiftgen.utils.*
import java.time.LocalDateTime

suspend fun ApplicationCall.getShifts() {
    structureId?.let { structureId ->
        val receive = receive<ShiftsReceive>()
        ShiftGenerator.arrangeTheWorkers(structureId, receive.yearMonth)
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

private enum class ActionResult {
    OK_ONE,
    OK_ALL,
    ERROR
}

private suspend fun insertToShifts(structureId: Int, receive: ShiftReceive, day: Int? = null): ActionResult =
    if (Shifts.insertShift(
            structureId,
            ShiftDTO(
                0,
                receive.name,
                receive.periodicity,
                receive.workerId,
                receive.directionId,
                receive.action,
                day?.let {
                    LocalDateTime.of(
                        receive.startTime.year,
                        receive.startTime.month,
                        day,
                        receive.startTime.hour,
                        receive.startTime.minute
                    )
                } ?: receive.startTime,
                receive.duration,
                receive.restDuration
            )
        )
    ) {
        ActionResult.OK_ONE
    } else {
        ActionResult.ERROR
    }

suspend fun ApplicationCall.insertShift() {
    structureId?.let { structureId ->
        val receive = receive<ShiftReceive>()
        val currentLocalDateTime = LocalDateTime.now()
        val nextYearMonth = currentLocalDateTime.toYearMonth().plusMonths(1)
        if (
            receive.startTime < receive.endTime()
            && (receive.startTime >= currentLocalDateTime || receive.startTime.toYearMonth() <= nextYearMonth)
        ) {
            val result: ActionResult = when (receive.periodicity) {
                Periodicity.ON_EVEN -> {
                    var startDay = receive.startTime.dayOfMonth
                    val endDay = receive.startTime.toYearMonth().lengthOfMonth()
                    if (startDay.toEvenInt() <= endDay) {
                        startDay = startDay.toEvenInt()
                        val resultList = (startDay..endDay step 2).map { day ->
                            insertToShifts(structureId, receive, day)
                        }
                        if (!resultList.contains(ActionResult.ERROR)) {
                            ActionResult.OK_ALL
                        } else {
                            ActionResult.ERROR
                        }
                    } else {
                        ActionResult.ERROR
                    }
                }

                Periodicity.ON_ODD -> {
                    var startDay = receive.startTime.dayOfMonth
                    val endDay = receive.startTime.toYearMonth().lengthOfMonth()
                    if (startDay.toOddInt() <= endDay) {
                        startDay = startDay.toOddInt()
                        val resultList = (startDay..endDay step 2).map { day ->
                            insertToShifts(structureId, receive, day)
                        }
                        if (!resultList.contains(ActionResult.ERROR)) {
                            ActionResult.OK_ALL
                        } else {
                            ActionResult.ERROR
                        }
                    } else {
                        ActionResult.ERROR
                    }
                }

                Periodicity.SINGLE -> {
                    insertToShifts(structureId, receive)
                }

                Periodicity.DAILY -> {
                    val startDay = receive.startTime.dayOfMonth
                    val endDay = receive.startTime.toYearMonth().lengthOfMonth()
                    val resultList = (startDay..endDay).map { day ->
                        insertToShifts(structureId, receive, day)
                    }
                    if (!resultList.contains(ActionResult.ERROR)) {
                        ActionResult.OK_ALL
                    } else {
                        ActionResult.ERROR
                    }
                }

                Periodicity.WEEKLY -> {
                    val startDay = receive.startTime.dayOfMonth
                    val endDay = receive.startTime.toYearMonth().lengthOfMonth()
                    val resultList = (startDay..endDay step 7).map { day ->
                        insertToShifts(structureId, receive, day)
                    }
                    if (!resultList.contains(ActionResult.ERROR)) {
                        ActionResult.OK_ALL
                    } else {
                        ActionResult.ERROR
                    }
                }
            }
            when (result) {
                ActionResult.OK_ONE -> {
                    respond(HttpStatusCode.OK, "Смена добавлена.")
                }

                ActionResult.OK_ALL -> {
                    respond(HttpStatusCode.OK, "Смены добавлены.")
                }

                ActionResult.ERROR -> {
                    respond(HttpStatusCode.InternalServerError, "Ошибка добавления смены.")
                }
            }
        } else {
            respond(HttpStatusCode.BadRequest, "Ошибка времени старта смены (неверный период).")
        }
    }
}

suspend fun ApplicationCall.updateShift() {
    structureId?.let { structureId ->
        val receive = receive<ShiftReceive>()
        val currentLocalDateTime = LocalDateTime.now()
        val nextYearMonth = currentLocalDateTime.toYearMonth().plusMonths(1)
        if (
            receive.startTime < receive.endTime()
            && (receive.startTime >= currentLocalDateTime || receive.startTime.toYearMonth() <= nextYearMonth)
        ) {
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
        } else {
            respond(HttpStatusCode.BadRequest, "Ошибка времени старта смены (неверный период).")
        }
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
            respond(HttpStatusCode.InternalServerError, "Список месяцев пуст.")
        }
    }
}
