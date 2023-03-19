package ru.shiftgen.databse.content.events

import kotlinx.serialization.Serializable

@Serializable
data class EventDTO(
    val id: Int = 0,
    val name: String,
    val directionId: Int,
    val structureId: Int,
    val timeBlocksIds: List<Int>
)