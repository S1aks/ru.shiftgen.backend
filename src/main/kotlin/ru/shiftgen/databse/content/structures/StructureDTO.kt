package ru.shiftgen.databse.content.structures

import kotlinx.serialization.Serializable

@Serializable
data class StructureDTO(
    val id: Int = 0,
    val name: String,
    val description: String?
)
