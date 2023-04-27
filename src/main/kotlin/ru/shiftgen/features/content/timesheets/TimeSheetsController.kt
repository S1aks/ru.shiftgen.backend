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
            this.respond(HttpStatusCode.InternalServerError, "Error getting timesheets data")
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
                this.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        } ?: this.respond(HttpStatusCode.InternalServerError, "Error getting timesheet data by id")
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
                this.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error getting timesheets data by worker id")
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
                this.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        } ?: this.respond(
            HttpStatusCode.InternalServerError,
            "Error getting timesheet data by worker id and year - month"
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
                this.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error getting timesheets data by year - month")
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
            this.respond(HttpStatusCode.OK, "TimeSheet data inserted")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error insert timesheet data")
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
            this.respond(HttpStatusCode.OK, "TimeSheet data updated")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error update timesheet data")
        }
    }
}

suspend fun ApplicationCall.deleteTimeSheet() {
    this.structureId?.let {
        val receive = this.receive<IdReceive>()
        if (TimeSheets.deleteTimeSheet(receive.id)) {
            this.respond(HttpStatusCode.OK, "TimeSheet data deleted")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error delete timesheet data")
        }
    }
}