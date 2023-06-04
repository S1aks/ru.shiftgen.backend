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

suspend fun ApplicationCall.getTimeSheetById() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        TimeSheets.getTimeSheetById(receive.id)?.let { timeSheet ->
            if (timeSheet.structureId == structureId) {
                respond(TimeSheetResponse(timeSheet))
            } else {
                respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } ?: respond(HttpStatusCode.InternalServerError, "Ошибка получения табеля рабочего времени по id.")
    }
}

suspend fun ApplicationCall.getTimeSheetsByWorkerId() {
    structureId?.let { structureId ->
        val receive = receive<IdReceive>()
        val list = TimeSheets.getTimeSheetsByWorkerId(receive.id)
        if (list.isNotEmpty()) {
            if (list.first().structureId == structureId) {
                respond(TimeSheetsResponse(list))
            } else {
                respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
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
            if (timeSheet.structureId == structureId) {
                respond(TimeSheetResponse(timeSheet))
            } else {
                respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
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
            if (list.first().structureId == structureId) {
                respond(TimeSheetsResponse(list))
            } else {
                respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } else {
            respond(
                HttpStatusCode.InternalServerError,
                "Ошибка получения табелей рабочего времени по рабочему месяцу."
            )
        }
    }
}

suspend fun ApplicationCall.insertTimeSheet() {
    structureId?.let { structureId ->
        val receive = receive<TimeSheetReceive>()
        if (TimeSheets.insertTimeSheet(
                TimeSheetDTO(
                    0,
                    receive.workerId,
                    structureId,
                    receive.yearMonth,
                    receive.workedTime,
                    receive.calculatedTime,
                    receive.correctionTime
                )
            )
        ) {
            respond(HttpStatusCode.OK, "Табель рабочего времени добавлен.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка добавления табеля рабочего времени.")
        }
    }
}

suspend fun ApplicationCall.updateTimeSheet() {
    structureId?.let { structureId ->
        val receive = receive<TimeSheetReceive>()
        if (TimeSheets.updateTimeSheet(
                TimeSheetDTO(
                    receive.id,
                    receive.workerId,
                    structureId,
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
    }
}

suspend fun ApplicationCall.deleteTimeSheet() {
    structureId?.let {
        val receive = receive<IdReceive>()
        if (TimeSheets.deleteTimeSheet(receive.id)) {
            respond(HttpStatusCode.OK, "Табель рабочего времени удален.")
        } else {
            respond(HttpStatusCode.InternalServerError, "Ошибка удаления табеля рабочего времени.")
        }
    }
}
