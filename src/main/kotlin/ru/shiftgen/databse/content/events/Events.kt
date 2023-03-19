package ru.shiftgen.databse.content.events

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.databse.content.directions.Directions
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object Events : Table(), EventsDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val name = varchar("name", 30)
    internal val directionId = reference("direction_id", Directions.id)
    internal val structureId = reference("structure_id", Structures.id)
    internal val timeBlocksIds = varchar("time_block_ids", 256)
    override val primaryKey = PrimaryKey(id, name = "PK_Event_Id")

    override suspend fun insertEvent(event: EventDTO): Boolean = dbQuery {
        Events.insert {
            it[name] = event.name
            it[directionId] = event.directionId
            it[structureId] = event.structureId
            it[timeBlocksIds] = event.timeBlocksIds.joinToString(",")
        }.insertedCount > 0
    }

    override suspend fun updateEvent(event: EventDTO): Boolean = dbQuery {
        Events.update({ id eq event.id }) {
            it[name] = event.name
            it[directionId] = event.directionId
            it[structureId] = event.structureId
            it[timeBlocksIds] = event.timeBlocksIds.joinToString(",")
        } > 0
    }

    override suspend fun getEvent(id: Int): EventDTO? = dbQuery {
        Events.select { Events.id eq id }.singleOrNull()?.toEventDTO()
    }

    override suspend fun getEvents(structureId: Int): List<EventDTO> = dbQuery {
        Events.select { Events.structureId eq structureId }.map { it.toEventDTO() }
    }

    override suspend fun deleteEvent(id: Int): Boolean = dbQuery {
        Events.deleteWhere { Events.id eq id } > 0
    }
}