package ru.shiftgen.databse.content.time_blocks

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object TimeBlocks : Table(), TimeBlocksDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val structureId = reference("structure_id", Structures.id)
    internal val name = varchar("name", 30)
    internal val duration = long("duration")
    internal val action = integer("action")
    override val primaryKey = PrimaryKey(id, name = "PK_TimeBlock_Id")

    override suspend fun insertTimeBlock(timeBlock: TimeBlockDTO): Boolean = dbQuery {
        TimeBlocks.insert {
            it[structureId] = timeBlock.structureId
            it[name] = timeBlock.name
            it[duration] = timeBlock.duration
            it[action] = timeBlock.action.ordinal
        }.insertedCount > 0
    }

    override suspend fun updateTimeBlock(timeBlock: TimeBlockDTO): Boolean = dbQuery {
        TimeBlocks.update({ id eq timeBlock.id }) {
            it[structureId] = timeBlock.structureId
            it[name] = timeBlock.name
            it[duration] = timeBlock.duration
            it[action] = timeBlock.action.ordinal
        } > 0
    }

    override suspend fun getTimeBlock(id: Int): TimeBlockDTO? = dbQuery {
        TimeBlocks.select { TimeBlocks.id eq id }.singleOrNull()?.toTimeBlockDTO()
    }

    override suspend fun getTimeBlocks(structureId: Int): List<TimeBlockDTO> = dbQuery {
        TimeBlocks.select { TimeBlocks.structureId eq structureId }.map { it.toTimeBlockDTO() }
    }

    override suspend fun deleteTimeBlock(id: Int): Boolean = dbQuery {
        TimeBlocks.deleteWhere { TimeBlocks.id eq id } > 0
    }
}