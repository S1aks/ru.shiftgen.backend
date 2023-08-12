package ru.shiftgen.databse.content.shifts

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.databse.content.directions.Directions
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.databse.content.workers.Workers
import ru.shiftgen.plugins.DatabaseFactory.dbQuery
import ru.shiftgen.utils.toYearMonth
import java.time.YearMonth

object Shifts : Table(), ShiftsDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val name = varchar("name", 30)
    internal val yearMonth = varchar("year_month", 8)
    internal val periodicity = integer("periodicity")
    internal val workerId = reference("worker_id", Workers.id).nullable()
    internal val structureId = reference("structure_id", Structures.id)
    internal val directionId = reference("direction_id", Directions.id)
    internal val action = integer("action")
    internal val startTime = varchar("start_time", 30)
    internal val duration = long("duration")
    internal val restDuration = long("rest_duration")
    override val primaryKey = PrimaryKey(id)

    override suspend fun insertShift(structureId: Int, shift: ShiftDTO): Boolean = dbQuery {
        Shifts.insert {
            it[name] = shift.name
            it[yearMonth] = shift.startTime.toYearMonth().toString()
            it[periodicity] = shift.periodicity.ordinal
            it[workerId] = shift.workerId
            it[this.structureId] = structureId
            it[directionId] = shift.directionId
            it[action] = shift.action.ordinal
            it[startTime] = shift.startTime.toString()
            it[duration] = shift.duration
            it[restDuration] = shift.restDuration
        }.insertedCount > 0
    }

    override suspend fun updateShift(shift: ShiftDTO): Boolean = dbQuery {
        Shifts.update({ id eq shift.id }) {
            it[name] = shift.name
            it[yearMonth] = shift.startTime.toYearMonth().toString()
            it[periodicity] = shift.periodicity.ordinal
            it[workerId] = shift.workerId
            it[directionId] = shift.directionId
            it[action] = shift.action.ordinal
            it[startTime] = shift.startTime.toString()
            it[duration] = shift.duration
            it[restDuration] = shift.restDuration
        } > 0
    }

    override suspend fun getShift(id: Int): ShiftDTO? = dbQuery {
        Shifts.select { Shifts.id eq id }.singleOrNull()?.toShiftDTO()
    }

    override suspend fun getShiftStructureId(id: Int): Int? = dbQuery {
        Shifts.select { Shifts.id eq id }.singleOrNull()?.shiftStructureId()
    }

    override suspend fun getShifts(structureId: Int, yearMonth: YearMonth): List<ShiftDTO> = dbQuery {
        Shifts.select { Shifts.structureId eq structureId and (Shifts.yearMonth eq yearMonth.toString()) }
            .orderBy(startTime)
            .map { it.toShiftDTO() }
    }

    override suspend fun getYearMonths(structureId: Int): List<String> = dbQuery {
        Shifts.select { Shifts.structureId eq structureId }.orderBy(yearMonth).map { it[yearMonth] }.distinct()
    }

    override suspend fun deleteShift(id: Int): Boolean = dbQuery {
        Shifts.deleteWhere { Shifts.id eq id } > 0
    }
}