package ru.shiftgen.features.content.events

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.shiftgen.databse.content.events.EventDTO
import ru.shiftgen.databse.content.events.Events
import ru.shiftgen.features.content.IdReceive
import ru.shiftgen.plugins.structureId

class EventsController(private val call: ApplicationCall) {
    suspend fun getEvents() {
        call.structureId?.let { structureId ->
            val list = Events.getEvents(structureId)
            if (list.isNotEmpty()) {
                call.respond(EventsResponse(list))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Error getting events data")
            }
        }
    }

    suspend fun getEvent() {
        call.structureId?.let { structureId ->
            val receive = call.receive<IdReceive>()
            Events.getEvent(receive.id)?.let { event ->
                if (event.structureId == structureId) {
                    call.respond(EventResponse(event))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Error in data structure id")
                }
            } ?: call.respond(HttpStatusCode.InternalServerError, "Error getting event data")
        }
    }

    suspend fun insertEvent() {
        call.structureId?.let { structureId ->
            val receive = call.receive<EventsReceive>()
            if (!Events.insertEvent(
                    EventDTO(0, receive.name, receive.directionId, structureId, receive.timeBlocksIds)
                )
            ) {
                call.respond(HttpStatusCode.InternalServerError, "Error insert direction data")
            }
        }
    }

    suspend fun updateEvent() {
        call.structureId?.let { structureId ->
            val receive = call.receive<EventsReceive>()
            if (!Events.updateEvent(
                    EventDTO(0, receive.name, receive.directionId, structureId, receive.timeBlocksIds)
                )
            ) {
                call.respond(HttpStatusCode.InternalServerError, "Error update direction data")
            }
        }
    }

    suspend fun deleteEvent() {
        call.structureId?.let {
            val receive = call.receive<IdReceive>()
            if (!Events.deleteEvent(receive.id)) {
                call.respond(HttpStatusCode.InternalServerError, "Error delete direction data")
            }
        }
    }
}