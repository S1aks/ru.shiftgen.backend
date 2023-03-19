package ru.shiftgen.databse.content.directions

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object Directions : Table(), DirectionsDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val name = varchar("name", 30).uniqueIndex()
    internal val structureId = reference("structure_id", Structures.id)
    override val primaryKey = PrimaryKey(id, name = "PK_Direction_Id")

    override suspend fun insertDirection(direction: DirectionDTO): Boolean = dbQuery {
        Directions.insert {
            it[name] = direction.name
            it[structureId] = direction.structureId
        }.insertedCount > 0
    }

    override suspend fun updateDirection(direction: DirectionDTO): Boolean = dbQuery {
        Directions.update({ id eq direction.id }) {
            it[name] = direction.name
            it[structureId] = direction.structureId
        } > 0
    }

    override suspend fun getDirection(id: Int): DirectionDTO? = dbQuery {
        Directions.select { Directions.id eq id }.singleOrNull()?.toDirectionDTO()
    }

    override suspend fun getDirections(structureId: Int): List<DirectionDTO> = dbQuery {
        Directions.select { Directions.structureId eq structureId }.map { it.toDirectionDTO() }
    }

    override suspend fun deleteDirection(id: Int): Boolean = dbQuery {
        Directions.deleteWhere { Directions.id eq id } > 0
    }
}