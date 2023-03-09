package ru.shiftgen.databse.content.time_blocks

import ru.shiftgen.databse.content.enums.Action

data class TimeBlockDTO(
    val id: Int = 0,
    val name: String,
    val duration: Long,
    val action: Action
)
