package ru.shiftgen.features.content.shifts

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.databse.content.shifts.Shifts
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

class ShiftsController(private val call: ApplicationCall) {
    suspend fun getShifts() {
        call.structureId?.let { structureId ->
            val receive = call.receive<ShiftsReceive>()
            val list = Shifts.getShifts(structureId, receive.periodYearMonth)
            if (list.isNotEmpty()) {
                call.respond(ShiftsResponse(list))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting shifts data")
            }
        }
    }

    suspend fun getShift() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            Shifts.getShift(receive.id)?.let { shift ->
                if (shift.structureId == structureId) {
                    call.respond(ShiftResponse(shift))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Structure Id match error")
                }
            } ?: call.respond(HttpStatusCode.InternalServerError, "Error getting shift data")
        }
    }

    suspend fun insertShift() {
        call.structureId?.let { structureId ->
            val receive = call.receive<ShiftReceive>()
            if (!Shifts.insertShift(
                    ShiftDTO(
                        0,
                        receive.name,
                        receive.periodYearMonth,
                        receive.periodicity,
                        receive.workerId,
                        structureId,
                        receive.directionId,
                        receive.startTime,
                        receive.timeBlocksIds
                    )
                )
            ) {
                call.respond(HttpStatusCode.InternalServerError, "Error insert shift data")
            }
        }
    }

    suspend fun updateShift() {
        call.structureId?.let { structureId ->
            val receive = call.receive<ShiftReceive>()
            if (!Shifts.updateShift(
                    ShiftDTO(
                        receive.id,
                        receive.name,
                        receive.periodYearMonth,
                        receive.periodicity,
                        receive.workerId,
                        structureId,
                        receive.directionId,
                        receive.startTime,
                        receive.timeBlocksIds
                    )
                )
            ) {
                call.respond(HttpStatusCode.InternalServerError, "Error update shift data")
            }
        }
    }

    suspend fun deleteShift() {
        call.structureId?.let {
            val receive = call.receive<IdReceive>()
            if (!Shifts.deleteShift(receive.id)) {
                call.respond(HttpStatusCode.InternalServerError, "Error delete shift data")
            }
        }
    }
}