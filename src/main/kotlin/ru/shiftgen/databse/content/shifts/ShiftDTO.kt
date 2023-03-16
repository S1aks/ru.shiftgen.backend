package ru.shiftgen.databse.content.shifts

import ru.shiftgen.databse.content.enums.Periodicity

data class ShiftDTO(
    val id: Int = 0,
    val name: String,
    val periodicity: Periodicity,
    val workerId: Int?,
    val structureId: Int,
    val startTime: Long,
    val eventId: Int
)
