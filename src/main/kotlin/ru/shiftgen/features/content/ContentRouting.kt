package ru.shiftgen.features.content

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.shiftgen.features.content.directions.*
import ru.shiftgen.features.content.shifts.*
import ru.shiftgen.features.content.structures.*
import ru.shiftgen.features.content.timesheets.*
import ru.shiftgen.features.content.workers.*

fun Application.configureContentRouting() {
    routing {
        get("/") { call.respond(HttpStatusCode.OK, "Server Ok!") }
        directionsRoute()
        shiftsRoute()
        structuresRoute()
        timeSheetsRoute()
        workersRoute()
    }
}

fun Route.directionsRoute() {
    authenticate("auth-jwt") {
        get("/directions") { call.getDirections() }
        post("/direction/get") { call.getDirection() }
        post("/direction/insert") { call.insertDirection() }
        post("/direction/update") { call.updateDirection() }
        post("/direction/delete") { call.deleteDirection() }
    }
}

fun Route.shiftsRoute() {
    authenticate("auth-jwt") {
        post("/shifts") { call.getShifts() }
        post("/shift/get") { call.getShift() }
        post("/shift/insert") { call.insertShift() }
        post("/shift/update") { call.updateShift() }
        post("/shift/delete") { call.deleteShift() }
        get("/shifts/yearmonths") { call.getYearMonths() }
    }
}

fun Route.structuresRoute() {
    get("/structures") { call.getStructures() }
    post("/structure/insert") { call.insertStructure() }
    authenticate("auth-jwt") {
        get("/structure_id") { call.getUserStructureId() }
        post("/structure/get") { call.getStructure() }
        post("/structure/update") { call.updateStructure() }
        post("/structure/delete") { call.deleteStructure() }
    }
}

fun Route.timeSheetsRoute() {
    authenticate("auth-jwt") {
        get("/timesheets") { call.getTimeSheets() }
        post("/timesheet/get_by_id") { call.getTimeSheetById() }
        post("/timesheet/get_by_worker_id") { call.getTimeSheetsByWorkerId() }
        post("/timesheet/get_by_worker_id_in_year_month") { call.getTimeSheetByWorkerIdInYearMonth() }
        post("/timesheet/get_by_year_month") { call.getTimeSheetsInYearMonth() }
        post("/timesheet/insert") { call.insertTimeSheet() }
        post("/timesheet/update") { call.updateTimeSheet() }
        post("/timesheet/delete") { call.deleteTimeSheet() }
    }
}

fun Route.workersRoute() {
    authenticate("auth-jwt") {
        get("/workers") { call.getWorkers() }
        post("/worker/get") { call.getWorker() }
        post("/worker/insert") { call.insertWorker() }
        post("/worker/update") { call.updateWorker() }
        post("/worker/delete") { call.deleteWorker() }
    }
}