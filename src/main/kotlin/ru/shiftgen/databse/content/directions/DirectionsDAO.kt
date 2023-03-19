package ru.shiftgen.databse.content.directions

import org.jetbrains.exposed.sql.ResultRow

interface DirectionsDAO {
    fun ResultRow.toDirectionDTO() = DirectionDTO(
        id = this[Directions.id],
        name = this[Directions.name],
        structureId = this[Directions.structureId]
    )

    suspend fun insertDirection(direction: DirectionDTO): Boolean
    suspend fun updateDirection(direction: DirectionDTO): Boolean
    suspend fun getDirection(id: Int): DirectionDTO?
    suspend fun getDirections(structureId: Int): List<DirectionDTO>
    suspend fun deleteDirection(id: Int): Boolean
}