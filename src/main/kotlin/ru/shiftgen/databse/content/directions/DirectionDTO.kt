package ru.shiftgen.databse.content.directions

import kotlinx.serialization.Serializable

@Serializable
data class DirectionDTO(
    val id: Int = 0,
    val name: String
)
