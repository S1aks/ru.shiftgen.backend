package ru.shiftgen.features.content.structures

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.structures.StructureDTO

@Serializable
data class StructureRequest(
    val id: Int,
    val name: String,
    val description: String?,
    val restHours: Int,
    val allowedConsecutiveNights: Int,
    val nightStartHour: Int,
    val nightEndHour: Int
)

@Serializable
data class StructureResponse(
    val structure: StructureDTO
)

@Serializable
data class StructuresResponse(
    val list: Map<Int, String>
)