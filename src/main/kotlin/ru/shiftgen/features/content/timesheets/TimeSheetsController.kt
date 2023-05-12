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
    this.structureId?.let { structureId ->
        val list = TimeSheets.getTimeSheets(structureId)
        if (list.isNotEmpty()) {
            this.respond(TimeSheetsResponse(list))
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка получения табелей рабочего времени.")
        }
    }
}

suspend fun ApplicationCall.getTimeSheetById() {
    this.structureId?.let { structureId ->
        val receive = this.receive<IdReceive>()
        TimeSheets.getTimeSheetById(receive.id)?.let { timeSheet ->
            if (timeSheet.structureId == structureId) {
                this.respond(TimeSheetResponse(timeSheet))
            } else {
                this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } ?: this.respond(HttpStatusCode.InternalServerError, "Ошибка получения табеля рабочего времени по id.")
    }
}

suspend fun ApplicationCall.getTimeSheetsByWorkerId() {
    this.structureId?.let { structureId ->
        val receive = this.receive<IdReceive>()
        val list = TimeSheets.getTimeSheetsByWorkerId(receive.id)
        if (list.isNotEmpty()) {
            if (list.first().structureId == structureId) {
                this.respond(TimeSheetsResponse(list))
            } else {
                this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } else {
            this.respond(
                HttpStatusCode.InternalServerError,
                "Ошибка получения табелей рабочего времени по id работника."
            )
        }
    }
}

suspend fun ApplicationCall.getTimeSheetByWorkerIdInYearMonth() {
    this.structureId?.let { structureId ->
        val receive = this.receive<TimeSheetsWorkerIdYearMonthRequest>()
        TimeSheets.getTimeSheetByWorkerIdInYearMonth(receive.workerId, receive.yearMonth)?.let { timeSheet ->
            if (timeSheet.structureId == structureId) {
                this.respond(TimeSheetResponse(timeSheet))
            } else {
                this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } ?: this.respond(
            HttpStatusCode.InternalServerError,
            "Ошибка получения табеля рабочего времени по id работника и по рабочему месяцу."
        )
    }
}

suspend fun ApplicationCall.getTimeSheetsInYearMonth() {
    this.structureId?.let { structureId ->
        val receive = this.receive<TimeSheetsYearMonthRequest>()
        val list = TimeSheets.getTimeSheetsInYearMonth(structureId, receive.yearMonth)
        if (list.isNotEmpty()) {
            if (list.first().structureId == structureId) {
                this.respond(TimeSheetsResponse(list))
            } else {
                this.respond(HttpStatusCode.BadRequest, "Ошибка соответствия id структуры.")
            }
        } else {
            this.respond(
                HttpStatusCode.InternalServerError,
                "Ошибка получения табелей рабочего времени по рабочему месяцу."
            )
        }
    }
}

suspend fun ApplicationCall.insertTimeSheet() {
    this.structureId?.let { structureId ->
        val receive = this.receive<TimeSheetRequest>()
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
            this.respond(HttpStatusCode.OK, "Табель рабочего времени добавлен.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка добавления табеля рабочего времени.")
        }
    }
}

suspend fun ApplicationCall.updateTimeSheet() {
    this.structureId?.let { structureId ->
        val receive = this.receive<TimeSheetRequest>()
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
            this.respond(HttpStatusCode.OK, "Табель рабочего времени обновлен.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка обновления табеля рабочего времени.")
        }
    }
}

suspend fun ApplicationCall.deleteTimeSheet() {
    this.structureId?.let {
        val receive = this.receive<IdReceive>()
        if (TimeSheets.deleteTimeSheet(receive.id)) {
            this.respond(HttpStatusCode.OK, "Табель рабочего времени удален.")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Ошибка удаления табеля рабочего времени.")
        }
    }
}