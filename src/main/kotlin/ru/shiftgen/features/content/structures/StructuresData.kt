package ru.shiftgen.features.content.structures

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.structures.StructureDTO

@Serializable
data class StructureReceive(
    val id: Int,
    val name: String,
    val description: String?
)

@Serializable
data class StructureResponse(
    val structure: StructureDTO
)

@Serializable
data class StructuresResponse(
    val list: List<StructureDTO>
)