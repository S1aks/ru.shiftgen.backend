package ru.shiftgen.databse.content.structures

import kotlinx.serialization.Serializable

@Serializable
data class StructureDTO(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val restHours: Int = 0,
    val allowedConsecutiveNights: Int = 0,
    val nightStartHour: Int = 0,
    val nightEndHour: Int = 6,
    val dispatcherPin: String = ""
)
