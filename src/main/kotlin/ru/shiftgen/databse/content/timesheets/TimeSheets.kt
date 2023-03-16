package ru.shiftgen.databse.content.timesheets

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.databse.content.workers.Workers
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object TimeSheets : Table(), TimeSheetsDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val workerId = reference("worker_id", Workers.id)
    internal val structureId = reference("structure_id", Structures.id)
    internal val periodYearMonth = varchar("period_year_month", 8)
    internal val workedTime = long("worked_time")
    internal val calculatedTime = long("calculated_time")
    internal val correctionTime = long("correction_time")
    override val primaryKey = PrimaryKey(id, name = "PK_WorkerTimeSheet_Id")

    override suspend fun insertTimeSheet(timeSheet: TimeSheetDTO): Boolean = dbQuery {
        TimeSheets.insert {
            it[workerId] = timeSheet.workerId
            it[structureId] = timeSheet.structureId
            it[periodYearMonth] = timeSheet.periodYearMonth.toString()
            it[workedTime] = timeSheet.workedTime
            it[calculatedTime] = timeSheet.calculatedTime
            it[correctionTime] = timeSheet.correctionTime
        }.insertedCount > 0
    }

    override suspend fun updateTimeSheet(timeSheet: TimeSheetDTO): Boolean = dbQuery {
        TimeSheets.update({ id eq timeSheet.id }) {
            it[workerId] = timeSheet.workerId
            it[structureId] = timeSheet.structureId
            it[periodYearMonth] = timeSheet.periodYearMonth.toString()
            it[workedTime] = timeSheet.workedTime
            it[calculatedTime] = timeSheet.calculatedTime
            it[correctionTime] = timeSheet.correctionTime
        } > 0
    }

    override suspend fun getTimeSheet(id: Int): TimeSheetDTO? = dbQuery {
        TimeSheets.select { TimeSheets.id eq id }.singleOrNull()?.toTimeSheetDTO()
    }

    override suspend fun deleteTimeSheet(id: Int): Boolean = dbQuery {
        TimeSheets.deleteWhere { TimeSheets.id eq id } > 0
    }
}