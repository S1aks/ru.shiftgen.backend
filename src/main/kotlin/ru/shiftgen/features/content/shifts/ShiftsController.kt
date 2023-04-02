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
        val receive = this.receive<ShiftsReceive>()
        val list = Shifts.getShifts(structureId, receive.periodYearMonth)
        if (list.isNotEmpty()) {
            this.respond(ShiftsResponse(list))
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error getting shifts data")
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
                this.respond(HttpStatusCode.BadRequest, "Structure Id match error")
            }
        } ?: this.respond(HttpStatusCode.InternalServerError, "Error getting shift data")
    }
}

suspend fun ApplicationCall.insertShift() {
    this.structureId?.let { structureId ->
        val receive = this.receive<ShiftReceive>()
        if (Shifts.insertShift(
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
            this.respond(HttpStatusCode.OK, "Shift data inserted")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error insert shift data")
        }
    }
}

suspend fun ApplicationCall.updateShift() {
    this.structureId?.let { structureId ->
        val receive = this.receive<ShiftReceive>()
        if (Shifts.updateShift(
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
            this.respond(HttpStatusCode.OK, "Shift data updated")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error update shift data")
        }
    }
}

suspend fun ApplicationCall.deleteShift() {
    this.structureId?.let {
        val receive = this.receive<IdReceive>()
        if (Shifts.deleteShift(receive.id)) {
            this.respond(HttpStatusCode.OK, "Shift data deleted")
        } else {
            this.respond(HttpStatusCode.InternalServerError, "Error delete shift data")
        }
    }
}