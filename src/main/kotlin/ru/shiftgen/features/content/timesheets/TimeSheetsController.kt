package ru.shiftgen.features.content.timesheets

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.timesheets.TimeSheetDTO
import ru.shiftgen.databse.content.timesheets.TimeSheets
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

suspend fun ApplicationCall.getTimeSheets() {
    structureId?.let { structureId ->
        val list = TimeSheets.getTimeSheets(structureId)
        if (list.isNotEmpty()) {
            respond(TimeSheetsResponse(list))
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка получения табелей рабочего времени.")
        }
    }
}

suspend fun ApplicationCall.getTimeSheet() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        TimeSheets.getTimeSheetStructureId(receive.id)?.let { sheetStructureId ->
            if (sheetStructureId == structureId) {
                TimeSheets.getTimeSheet(receive.id)?.let { timeSheet ->
                    respond(TimeSheetResponse(timeSheet))
                } ?: respond(HttpStatusCode.InternalServerError, "Ошибка получения табеля рабочего времени по id.")
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}

suspend fun ApplicationCall.getTimeSheetsByWorkerId() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        val list = TimeSheets.getTimeSheetsByWorkerId(receive.id)
        if (list.isNotEmpty()) {
            TimeSheets.getTimeSheetStructureId(list.first().id)?.let { sheetStructureId ->
                if (sheetStructureId == structureId) {
                    respond(TimeSheetsResponse(list))
                } else {
                    null
                }
            } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        } else {
            respond(
                HttpStatusCode.InternalServerError,
                "Ошибка получения табелей рабочего времени по id работника."
            )
        }
    }
}

suspend fun ApplicationCall.getTimeSheetByWorkerIdInYearMonth() {
    structureId?.let { structureId ->
        val receive = receive<TimeSheetsWorkerIdYearMonthReceive>()
        TimeSheets.getTimeSheetByWorkerIdInYearMonth(receive.workerId, receive.yearMonth)?.let { timeSheet ->
            TimeSheets.getTimeSheetStructureId(timeSheet.id)?.let { sheetStructureId ->
                if (sheetStructureId == structureId) {
                    respond(TimeSheetResponse(timeSheet))
                } else {
                    null
                }
            } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        } ?: respond(
            HttpStatusCode.InternalServerError,
            "Ошибка получения табеля рабочего времени по id работника и по рабочему месяцу."
        )
    }
}

suspend fun ApplicationCall.getTimeSheetsInYearMonth() {
    structureId?.let { structureId ->
        val receive = receive<TimeSheetsYearMonthReceive>()
        val list = TimeSheets.getTimeSheetsInYearMonth(structureId, receive.yearMonth)
        if (list.isNotEmpty()) {
            TimeSheets.getTimeSheetStructureId(list.first().id)?.let { sheetStructureId ->
                if (sheetStructureId == structureId) {
                    respond(TimeSheetsResponse(list))
                } else {
                    null
                }
            } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
        } else {
            respond(
                HttpStatusCode.InternalServerError,
                "Ошибка получения табелей рабочего времени по рабочему месяцу."
            )
        }
    }
}

//suspend fun ApplicationCall.insertTimeSheet() {
//    structureId?.let { structureId ->
//        val receive = receive<TimeSheetReceive>()
//        if (TimeSheets.insertTimeSheet(
//                structureId,
//                TimeSheetDTO(
//                    0,
//                    receive.workerId,
//                    receive.yearMonth,
//                    receive.workedTime,
//                    receive.calculatedTime,
//                    receive.correctionTime
//                )
//            )
//        ) {
//            respond(HttpStatusCode.OK, "Табель рабочего времени добавлен.")
//        } else {
//            respond(HttpStatusCode.InternalServerError, "Ошибка добавления табеля рабочего времени.")
//        }
//    }
//}

suspend fun ApplicationCall.updateTimeSheet() {
    structureId?.let { structureId ->
        val receive = receive<TimeSheetReceive>()
        TimeSheets.getTimeSheetStructureId(receive.id)?.let { timesheetStructureId ->
            if (timesheetStructureId == structureId) {
                if (TimeSheets.updateTimeSheet(
                        TimeSheetDTO(
                            receive.id,
                            receive.workerId,
                            receive.yearMonth,
                            receive.workedTime,
                            receive.calculatedTime,
                            receive.correctionTime
                        )
                    )
                ) {
                    respond(HttpStatusCode.OK, "Табель рабочего времени обновлен.")
                } else {
                    respond(HttpStatusCode.InternalServerError, "Ошибка обновления табеля рабочего времени.")
                }
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}

suspend fun ApplicationCall.deleteTimeSheet() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        TimeSheets.getTimeSheetStructureId(receive.id)?.let { timesheetStructureId ->
            if (timesheetStructureId == structureId) {
                if (TimeSheets.deleteTimeSheet(receive.id)) {
                    respond(HttpStatusCode.OK, "Табель рабочего времени удален.")
                } else {
                    respond(HttpStatusCode.InternalServerError, "Ошибка удаления табеля рабочего времени.")
                }
            } else {
                null
            }
        } ?: respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
    }
}
