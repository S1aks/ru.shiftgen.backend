package ru.shiftgen.databse.content.time_blocks

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.enums.Action

@Serializable
data class TimeBlockDTO(
    val id: Int = 0,
    val structureId: Int,
    val name: String,
    val duration: Long,
    val action: Action
)
