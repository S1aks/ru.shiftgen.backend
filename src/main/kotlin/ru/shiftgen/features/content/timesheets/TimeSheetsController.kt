package ru.shiftgen.features.content.timesheets

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.timesheets.TimeSheetDTO
import ru.shiftgen.databse.content.timesheets.TimeSheets
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

class TimeSheetsController(private val call: ApplicationCall) {
    suspend fun getTimeSheets() {
        call.structureId?.let { structureId ->
            val list = TimeSheets.getTimeSheets(structureId)
            if (list.isNotEmpty()) {
                call.respond(TimeSheetsResponse(list))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting timesheets data")
            }
        }
    }

    suspend fun getTimeSheetById() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            TimeSheets.getTimeSheetById(receive.id)?.let { timeSheet ->
                if (timeSheet.structureId == structureId) {
                    call.respond(TimeSheetResponse(timeSheet))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
                }
            } ?: call.respond(HttpStatusCode.InternalServerError, "Error getting timesheet data by id")
        }
    }

    suspend fun getTimeSheetsByWorkerId() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            val list = TimeSheets.getTimeSheetsByWorkerId(receive.id)
            if (list.isNotEmpty()) {
                if (list.first().structureId == structureId) {
                    call.respond(TimeSheetsResponse(list))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
                }
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting timesheets data by worker id")
            }
        }
    }

    suspend fun getTimeSheetByWorkerIdInYearMonth() {
        call.structureId?.let { structureId ->
            val receive = call.receive<TimeSheetsWorkerIdYearMonthReceive>()
            TimeSheets.getTimeSheetByWorkerIdInYearMonth(receive.workerId, receive.periodYearMonth)?.let { timeSheet ->
                if (timeSheet.structureId == structureId) {
                    call.respond(TimeSheetResponse(timeSheet))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
                }
            } ?: call.respond(
                HttpStatusCode.InternalServerError,
                "Error getting timesheet data by worker id and year - month"
            )
        }
    }

    suspend fun getTimeSheetsInYearMonth() {
        call.structureId?.let { structureId ->
            val receive = call.receive<TimeSheetsYearMonthReceive>()
            val list = TimeSheets.getTimeSheetsInYearMonth(structureId, receive.periodYearMonth)
            if (list.isNotEmpty()) {
                if (list.first().structureId == structureId) {
                    call.respond(TimeSheetsResponse(list))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
                }
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting timesheets data by year - month")
            }
        }
    }

    suspend fun insertTimeSheet() {
        call.structureId?.let { structureId ->
            val receive = call.receive<TimeSheetReceive>()
            if (!TimeSheets.insertTimeSheet(
                    TimeSheetDTO(
                        0,
                        receive.workerId,
                        structureId,
                        receive.periodYearMonth,
                        receive.workedTime,
                        receive.calculatedTime,
                        receive.correctionTime
                    )
                )
            ) {
                call.respond(HttpStatusCode.InternalServerError, "Error insert timesheet data")
            }
        }
    }

    suspend fun updateTimeSheet() {
        call.structureId?.let { structureId ->
            val receive = call.receive<TimeSheetReceive>()
            if (!TimeSheets.updateTimeSheet(
                    TimeSheetDTO(
                        receive.id,
                        receive.workerId,
                        structureId,
                        receive.periodYearMonth,
                        receive.workedTime,
                        receive.calculatedTime,
                        receive.correctionTime
                    )
                )
            ) {
                call.respond(HttpStatusCode.InternalServerError, "Error update timesheet data")
            }
        }
    }

    suspend fun deleteTimeSheet() {
        call.structureId?.let {
            val receive = call.receive<IdReceive>()
            if (!TimeSheets.deleteTimeSheet(receive.id)) {
                call.respond(HttpStatusCode.InternalServerError, "Error delete timesheet data")
            }
        }
    }
}