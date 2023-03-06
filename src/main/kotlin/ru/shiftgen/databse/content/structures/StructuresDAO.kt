package ru.shiftgen.databse.content.structures

import org.jetbrains.exposed.sql.ResultRow

interface StructuresDAO {
    fun ResultRow.toStructureDTO() = StructureDTO(
        id = this[Structures.id],
        name = this[Structures.name],
        description = this[Structures.description]
    )

    suspend fun insertStructure(structure: StructureDTO): Boolean
    suspend fun updateStructure(structure: StructureDTO): Boolean
    suspend fun getStructure(id: Int): StructureDTO?
    suspend fun deleteStructure(id: Int): Boolean
}