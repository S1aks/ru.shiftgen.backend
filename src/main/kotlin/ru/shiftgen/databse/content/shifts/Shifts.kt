package ru.shiftgen.databse.content.shifts

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.databse.content.events.Events
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.databse.content.workers.Workers
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object Shifts : Table(), ShiftsDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val name = varchar("name", 30)
    internal val periodicity = integer("periodicity")
    internal val workerId = reference("worker_id", Workers.id).nullable()
    internal val structureId = reference("structure_id", Structures.id)
    internal val startTime = long("start_time")
    internal val eventId = reference("event_id", Events.id)
    override val primaryKey = PrimaryKey(id, name = "PK_Shift_Id")

    override suspend fun insertShift(shift: ShiftDTO): Boolean = dbQuery {
        Shifts.insert {
            it[name] = shift.name
            it[periodicity] = shift.periodicity.ordinal
            it[workerId] = shift.workerId
            it[structureId] = shift.structureId
            it[startTime] = shift.startTime
            it[eventId] = shift.eventId
        }.insertedCount > 0
    }

    override suspend fun updateShift(shift: ShiftDTO): Boolean = dbQuery {
        Shifts.update({ id eq shift.id }) {
            it[name] = shift.name
            it[periodicity] = shift.periodicity.ordinal
            it[workerId] = shift.workerId
            it[structureId] = shift.structureId
            it[startTime] = shift.startTime
            it[eventId] = shift.eventId
        } > 0
    }

    override suspend fun getShift(id: Int): ShiftDTO? = dbQuery {
        Shifts.select { Shifts.id eq id }.singleOrNull()?.toShiftDTO()
    }

    override suspend fun deleteShift(id: Int): Boolean = dbQuery {
        Shifts.deleteWhere { Shifts.id eq id } > 0
    }
}