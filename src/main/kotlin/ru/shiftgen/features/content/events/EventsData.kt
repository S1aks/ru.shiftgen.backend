package ru.shiftgen.features.content.events

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.events.EventDTO

@Serializable
data class EventReceive(
    val id: Int,
    val name: String,
    val directionId: Int,
    val structureId: Int,
    val timeBlocksIds: List<Int>
)

@Serializable
data class EventResponse(
    val event: EventDTO
)

@Serializable
data class EventsResponse(
    val list: List<EventDTO>
)