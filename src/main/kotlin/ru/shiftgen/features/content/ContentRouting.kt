package ru.shiftgen.features.content

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import ru.shiftgen.features.content.directions.DirectionsController
import ru.shiftgen.features.content.shifts.ShiftsController
import ru.shiftgen.features.content.structures.StructuresController
import ru.shiftgen.features.content.time_blocks.TimeBlocksController
import ru.shiftgen.features.content.timesheets.TimeSheetsController
import ru.shiftgen.features.content.workers.WorkersController

fun Application.configureContentRouting() {

    routing {
        authenticate("auth-jwt") {
            get("/directions") { DirectionsController(call).getDirections() }
            get("/direction/get") { DirectionsController(call).getDirection() }
            post("/direction/insert") { DirectionsController(call).insertDirection() }
            post("/direction/update") { DirectionsController(call).updateDirection() }
            post("/direction/delete") { DirectionsController(call).deleteDirection() }

            get("/shifts") { ShiftsController(call).getShifts() }
            get("/shift/get") { ShiftsController(call).getShift() }
            post("/shift/insert") { ShiftsController(call).insertShift() }
            post("/shift/update") { ShiftsController(call).updateShift() }
            post("/shift/delete") { ShiftsController(call).deleteShift() }

            get("/structures") { StructuresController(call).getStructures() }
            get("/structure/get") { StructuresController(call).getStructure() }
            post("/structure/insert") { StructuresController(call).insertStructure() }
            post("/structure/update") { StructuresController(call).updateStructure() }
            post("/structure/delete") { StructuresController(call).deleteStructure() }

            get("/time_blocks") { TimeBlocksController(call).getTimeBlocks() }
            get("/time_block/get") { TimeBlocksController(call).getTimeBlock() }
            post("/time_block/insert") { TimeBlocksController(call).insertTimeBlock() }
            post("/time_block/update") { TimeBlocksController(call).updateTimeBlock() }
            post("/time_block/delete") { TimeBlocksController(call).deleteTimeBlock() }

            get("/timesheets") { TimeSheetsController(call).getTimeSheets() }
            get("/timesheet/get_by_id") { TimeSheetsController(call).getTimeSheetById() }
            get("/timesheet/get_by_worker_id") { TimeSheetsController(call).getTimeSheetsByWorkerId() }
            get("/timesheet/get_by_worker_id_in_year_month") { TimeSheetsController(call).getTimeSheetByWorkerIdInYearMonth() }
            get("/timesheet/get_by_year_month") { TimeSheetsController(call).getTimeSheetsInYearMonth() }
            post("/timesheet/insert") { TimeSheetsController(call).insertTimeSheet() }
            post("/timesheet/update") { TimeSheetsController(call).updateTimeSheet() }
            post("/timesheet/delete") { TimeSheetsController(call).deleteTimeSheet() }

            get("/workers") { WorkersController(call).getWorkers() }
            get("/worker/get") { WorkersController(call).getWorker() }
            post("/worker/insert") { WorkersController(call).insertWorker() }
            post("/worker/update") { WorkersController(call).updateWorker() }
            post("/worker/delete") { WorkersController(call).deleteWorker() }
        }
    }
}
