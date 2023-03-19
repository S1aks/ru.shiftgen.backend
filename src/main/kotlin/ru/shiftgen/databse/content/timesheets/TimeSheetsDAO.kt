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
    suspend fun getTimeSheet(id: Int): TimeSheetDTO?
    suspend fun getTimeSheets(structureId: Int): List<TimeSheetDTO>
    suspend fun deleteTimeSheet(id: Int): Boolean
}