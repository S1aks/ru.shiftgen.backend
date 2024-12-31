package ru.shiftgen.databse.content.structures

import kotlinx.serialization.Serializable

@Serializable
data class StructureDTO(
    val id: Int,
    val name: String,
    val description: String,
    val restHours: Int,
    val allowedConsecutiveNights: Int,
    val nightStartHour: Int,
    val nightEndHour: Int,
    val dispatcherPin: String
)
