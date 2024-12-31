package ru.shiftgen.databse.content.structures

import org.jetbrains.exposed.sql.ResultRow

interface StructuresDAO {
    fun ResultRow.toStructureDTO() = StructureDTO(
        id = this[Structures.id],
        name = this[Structures.name],
        description = this[Structures.description],
        restHours = this[Structures.restHours],
        allowedConsecutiveNights = this[Structures.allowedConsecutiveNights],
        nightStartHour = this[Structures.nightStartHour],
        nightEndHour = this[Structures.nightEndHour],
        dispatcherPin = this[Structures.dispatcherPin]
    )

    suspend fun ifStructureExists(id: Int): Boolean
    suspend fun insertStructure(structure: StructureDTO): Boolean
    suspend fun updateStructure(structure: StructureDTO): Boolean
    suspend fun getStructure(id: Int): StructureDTO?
    suspend fun getStructures(): List<StructureDTO>
    suspend fun deleteStructure(id: Int): Boolean
}