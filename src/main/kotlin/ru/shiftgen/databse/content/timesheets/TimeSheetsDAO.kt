package ru.shiftgen.databse.content.timesheets

import org.jetbrains.exposed.sql.ResultRow
import java.time.YearMonth

interface TimeSheetsDAO {
    fun ResultRow.toTimeSheetDTO() = TimeSheetDTO(
        id = this[TimeSheets.id],
        workerId = this[TimeSheets.workerId],
        structureId = this[TimeSheets.structureId],
        periodYearMonth = YearMonth.parse(this[TimeSheets.periodYearMonth]),
        workedTime = this[TimeSheets.workedTime],
        calculatedTime = this[TimeSheets.calculatedTime],
        correctionTime = this[TimeSheets.correctionTime]
    )

    suspend fun insertTimeSheet(timeSheet: TimeSheetDTO): Boolean
    suspend fun updateTimeSheet(timeSheet: TimeSheetDTO): Boolean
    suspend fun getTimeSheetById(id: Int): TimeSheetDTO?
    suspend fun getTimeSheetsByWorkerId(workerId: Int): List<TimeSheetDTO>
    suspend fun getTimeSheetByWorkerIdInYearMonth(workerId: Int, periodYearMonth: YearMonth): TimeSheetDTO?
    suspend fun getTimeSheets(structureId: Int): List<TimeSheetDTO>
    suspend fun getTimeSheetsInYearMonth(structureId: Int, periodYearMonth: YearMonth): List<TimeSheetDTO>
    suspend fun deleteTimeSheet(id: Int): Boolean
}