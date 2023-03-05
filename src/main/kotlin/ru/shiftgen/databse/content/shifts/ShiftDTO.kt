package ru.shiftgen.databse.content.shifts

data class ShiftDTO(
    val id: Int,
    val name: String,
    val periodicity: Int,
    val workerId: Int,
    val startTime: String,
    val eventId: Int
)
