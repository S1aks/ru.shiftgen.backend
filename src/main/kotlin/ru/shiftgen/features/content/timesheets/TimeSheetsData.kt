package ru.shiftgen.features.content.timesheets

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.timesheets.TimeSheetDTO
import ru.shiftgen.utils.YearMonthSerializer
import java.time.YearMonth

@Serializable
data class TimeSheetRequest(
    val id: Int,
    val workerId: Int,
    val structureId: Int,
    @Serializable(with = YearMonthSerializer::class)
    val yearMonth: YearMonth,
    val workedTime: Long,
    val calculatedTime: Long,
    val correctionTime: Long
)

@Serializable
data class TimeSheetsWorkerIdYearMonthRequest(
    val workerId: Int,
    @Serializable(with = YearMonthSerializer::class)
    val yearMonth: YearMonth
)

@Serializable
data class TimeSheetsYearMonthRequest(
    @Serializable(with = YearMonthSerializer::class)
    val yearMonth: YearMonth
)

@Serializable
data class TimeSheetResponse(
    val timesheet: TimeSheetDTO
)

@Serializable
data class TimeSheetsResponse(
    val list: List<TimeSheetDTO>
)