package ru.shiftgen.databse.content.timesheets

import org.jetbrains.exposed.sql.ResultRow
import java.time.YearMonth

interface TimeSheetsDAO {
    fun ResultRow.toTimeSheetDTO() = TimeSheetDTO(
        id = this[TimeSheets.id],
        workerId = this[TimeSheets.workerId],
        yearMonth = YearMonth.parse(this[TimeSheets.yearMonth]),
        workedTime = this[TimeSheets.workedTime],
        calculatedTime = this[TimeSheets.calculatedTime],
        correctionTime = this[TimeSheets.correctionTime]
    )

    fun ResultRow.timesheetStructureId() = this[TimeSheets.structureId]

    suspend fun insertTimeSheet(structureId: Int, timeSheet: TimeSheetDTO): Int?
    suspend fun updateTimeSheet(timeSheet: TimeSheetDTO): Boolean
    suspend fun getTimeSheet(id: Int): TimeSheetDTO?
    suspend fun getTimeSheetsByWorkerId(workerId: Int): List<TimeSheetDTO>
    suspend fun getTimeSheetByWorkerIdInYearMonth(workerId: Int, yearMonth: YearMonth): TimeSheetDTO?
    suspend fun getTimeSheets(structureId: Int): List<TimeSheetDTO>
    suspend fun getTimeSheetsInYearMonth(structureId: Int, yearMonth: YearMonth): List<TimeSheetDTO>
    suspend fun getTimeSheetStructureId(id: Int): Int?
    suspend fun deleteTimeSheet(id: Int): Boolean
}