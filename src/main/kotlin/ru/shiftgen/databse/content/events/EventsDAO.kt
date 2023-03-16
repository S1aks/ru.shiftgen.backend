package ru.shiftgen.databse.content.events

import org.jetbrains.exposed.sql.ResultRow

interface EventsDAO {
    fun ResultRow.toEventDTO() = EventDTO(
        id = this[Events.id],
        name = this[Events.name],
        directionId = this[Events.directionId],
        structureId = this[Events.structureId],
        timeBlocksIds = this[Events.timeBlocksIds].split(",").map { it.toInt() }
    )

    suspend fun insertEvent(event: EventDTO): Boolean
    suspend fun updateEvent(event: EventDTO): Boolean
    suspend fun getEvent(id: Int): EventDTO?
    suspend fun deleteEvent(id: Int): Boolean
}