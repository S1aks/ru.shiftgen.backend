package ru.shiftgen.databse.content.structures

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object Structures : Table(), StructuresDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val name = varchar("name", 25).uniqueIndex()
    internal val description = varchar("description", 256).nullable()
    internal val restHours = integer("rest_hours")
    internal val allowedConsecutiveNights = integer("allowed_consecutive_nights")
    internal val nightStartHour = integer("night_start_hour")
    internal val nightEndHour = integer("night_end_hour")
    override val primaryKey = PrimaryKey(id)

    override suspend fun ifStructureExists(id: Int): Boolean = dbQuery {
        Structures.select { Structures.id eq id }.singleOrNull() != null
    }

    override suspend fun insertStructure(structure: StructureDTO): Boolean = dbQuery {
        Structures.insert {
            it[name] = structure.name
            it[description] = structure.description
            it[restHours] = structure.restHours
            it[allowedConsecutiveNights] = structure.allowedConsecutiveNights
            it[nightStartHour] = structure.nightStartHour
            it[nightEndHour] = structure.nightEndHour
        }.insertedCount > 0
    }

    override suspend fun updateStructure(structure: StructureDTO): Boolean = dbQuery {
        Structures.update({ id eq structure.id }) {
            it[name] = structure.name
            it[description] = structure.description
            it[restHours] = structure.restHours
            it[allowedConsecutiveNights] = structure.allowedConsecutiveNights
            it[nightStartHour] = structure.nightStartHour
            it[nightEndHour] = structure.nightEndHour
        } > 0
    }

    override suspend fun getStructure(id: Int): StructureDTO? = dbQuery {
        Structures.select(Structures.id eq id).singleOrNull()?.toStructureDTO()
    }

    override suspend fun getStructures(): List<StructureDTO> = dbQuery {
        Structures.selectAll().orderBy(name).map { it.toStructureDTO() }
    }

    override suspend fun deleteStructure(id: Int): Boolean = dbQuery {
        Structures.deleteWhere { Structures.id eq id } > 0
    }
}