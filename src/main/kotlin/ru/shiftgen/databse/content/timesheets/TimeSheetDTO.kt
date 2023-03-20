package ru.shiftgen.databse.content.timesheets

import kotlinx.serialization.Serializable
import ru.shiftgen.utils.YearMonthSerializer
import java.time.YearMonth

@Serializable
data class TimeSheetDTO(
    val id: Int = 0,
    val workerId: Int,
    val structureId: Int,
    @Serializable(with = YearMonthSerializer::class)
    val periodYearMonth: YearMonth,
    val workedTime: Long = 0L,
    val calculatedTime: Long = 0L,
    val correctionTime: Long = 0L
)
