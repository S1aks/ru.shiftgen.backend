package ru.shiftgen.features.content.directions

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.directions.DirectionDTO

@Serializable
data class DirectionReceive(
    val id: Int,
    val name: String
)

@Serializable
data class DirectionsResponse(
    val list: List<DirectionDTO>
)