package ru.shiftgen.databse.content.structures

import org.jetbrains.exposed.sql.Table
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object Structures : Table(), StructuresDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val name = varchar("name", 25).uniqueIndex()
    internal val description = varchar("description", 256).nullable()

    override suspend fun insertStructure(token: StructureDTO): Boolean = dbQuery {
        TODO("Not yet implemented")
    }

    override suspend fun updateStructure(token: StructureDTO): Boolean = dbQuery {
        TODO("Not yet implemented")
    }

    override suspend fun getStructure(id: Int): StructureDTO? = dbQuery {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStructure(id: Int): Boolean = dbQuery {
        TODO("Not yet implemented")
    }

}