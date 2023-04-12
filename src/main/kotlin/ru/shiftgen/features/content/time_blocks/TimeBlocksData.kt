package ru.shiftgen.features.content.time_blocks

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.enums.Action
import ru.shiftgen.databse.content.time_blocks.TimeBlockDTO

@Serializable
data class TimeBlockRequest(
    val id: Int,
    val structureId: Int,
    val name: String,
    val duration: Long,
    val action: Action
)

@Serializable
data class TimeBlockResponse(
    val timeBlock: TimeBlockDTO
)

@Serializable
data class TimeBlocksResponse(
    val list: List<TimeBlockDTO>
)