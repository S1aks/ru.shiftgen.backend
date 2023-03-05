package ru.shiftgen.databse.content.structures

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object Structures : Table(), StructuresDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val name = varchar("name", 25).uniqueIndex()
    internal val description = varchar("description", 256).nullable()

    override suspend fun insertStructure(structure: StructureDTO): Boolean = dbQuery {
        Structures.insert {
            it[name] = structure.name
            it[description] = structure.description
        }.insertedCount > 0
    }

    override suspend fun updateStructure(structure: StructureDTO): Boolean = dbQuery {
        Structures.update({ id eq structure.id }) {
            it[name] = structure.name
            it[description] = structure.description
        } > 0
    }

    override suspend fun getStructure(id: Int): StructureDTO? = dbQuery {
        Structures.select(Structures.id eq id)
            .singleOrNull()
            ?.toStructureDTO()
    }

    override suspend fun deleteStructure(id: Int): Boolean = dbQuery {
        Structures.deleteWhere { Structures.id eq id } > 0
    }

}