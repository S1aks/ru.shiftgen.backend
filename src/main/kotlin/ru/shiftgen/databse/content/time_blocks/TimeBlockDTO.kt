package ru.shiftgen.databse.content.time_blocks

data class TimeBlockDTO(
    val id: Int,
    val name: String,
    val duration: Long,
    val action: Int
)
