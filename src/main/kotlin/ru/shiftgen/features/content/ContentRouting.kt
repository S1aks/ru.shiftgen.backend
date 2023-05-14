package ru.shiftgen.features.content

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.shiftgen.features.content.directions.*
import ru.shiftgen.features.content.shifts.*
import ru.shiftgen.features.content.structures.*
import ru.shiftgen.features.content.time_blocks.*
import ru.shiftgen.features.content.timesheets.*
import ru.shiftgen.features.content.workers.*

fun Application.configureContentRouting() {
    routing {
        get("/") { call.respond(HttpStatusCode.OK, "Server Ok!") }
        directionsRoute()
        shiftsRoute()
        structuresRoute()
        timeBlocksRoute()
        timeSheetsRoute()
        workersRoute()
    }
}

fun Route.directionsRoute() {
    authenticate {
        get("/directions") { call.getDirections() }
        get("/direction/get") { call.getDirection() }
        post("/direction/insert") { call.insertDirection() }
        post("/direction/update") { call.updateDirection() }
        post("/direction/delete") { call.deleteDirection() }
    }
}

fun Route.shiftsRoute() {
    authenticate {
        get("/shifts") { call.getShifts() }
        get("/shift/get") { call.getShift() }
        post("/shift/insert") { call.insertShift() }
        post("/shift/update") { call.updateShift() }
        post("/shift/delete") { call.deleteShift() }
    }
}

fun Route.structuresRoute() {
    get("/structures") { call.getStructures() }
    post("/structure/insert") { call.insertStructure() }
    authenticate {
        get("/structure/get") { call.getStructure() }
        post("/structure/update") { call.updateStructure() }
        post("/structure/delete") { call.deleteStructure() }
    }
}

fun Route.timeBlocksRoute() {
    authenticate {
        get("/time_blocks") { call.getTimeBlocks() }
        get("/time_block/get") { call.getTimeBlock() }
        post("/time_block/insert") { call.insertTimeBlock() }
        post("/time_block/update") { call.updateTimeBlock() }
        post("/time_block/delete") { call.deleteTimeBlock() }
    }
}

fun Route.timeSheetsRoute() {
    authenticate {
        get("/timesheets") { call.getTimeSheets() }
        get("/timesheet/get_by_id") { call.getTimeSheetById() }
        get("/timesheet/get_by_worker_id") { call.getTimeSheetsByWorkerId() }
        get("/timesheet/get_by_worker_id_in_year_month") { call.getTimeSheetByWorkerIdInYearMonth() }
        get("/timesheet/get_by_year_month") { call.getTimeSheetsInYearMonth() }
        post("/timesheet/insert") { call.insertTimeSheet() }
        post("/timesheet/update") { call.updateTimeSheet() }
        post("/timesheet/delete") { call.deleteTimeSheet() }
    }
}

fun Route.workersRoute() {
    authenticate {
        get("/workers") { call.getWorkers() }
        get("/worker/get") { call.getWorker() }
        post("/worker/insert") { call.insertWorker() }
        post("/worker/update") { call.updateWorker() }
        post("/worker/delete") { call.deleteWorker() }
    }
}