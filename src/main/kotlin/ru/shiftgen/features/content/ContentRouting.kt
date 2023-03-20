package ru.shiftgen.features.content

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import ru.shiftgen.features.content.directions.DirectionsController
import ru.shiftgen.features.content.events.EventsController
import ru.shiftgen.features.content.shifts.ShiftsController
import ru.shiftgen.features.content.structures.StructuresController

fun Application.configureContentRouting() {

    routing {
        authenticate("auth-jwt") {

            get("/directions") { DirectionsController(call).getDirections() }
            get("/direction/get") { DirectionsController(call).getDirection() }
            post("/direction/insert") { DirectionsController(call).insertDirection() }
            post("/direction/update") { DirectionsController(call).updateDirection() }
            post("/direction/delete") { DirectionsController(call).deleteDirection() }

            get("/events") { EventsController(call).getEvents() }
            get("/event/get") { EventsController(call).getEvent() }
            post("/event/insert") { EventsController(call).insertEvent() }
            post("/event/update") { EventsController(call).updateEvent() }
            post("/event/delete") { EventsController(call).deleteEvent() }

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
        }
    }
}
