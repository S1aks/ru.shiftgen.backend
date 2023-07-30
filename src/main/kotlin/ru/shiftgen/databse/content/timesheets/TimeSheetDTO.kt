package ru.shiftgen.databse.content.timesheets

import kotlinx.serialization.Serializable
import ru.shiftgen.utils.YearMonthSerializer
import java.time.YearMonth

@Serializable
data class TimeSheetDTO(
    val id: Int = 0,
    val workerId: Int,
    @Serializable(with = YearMonthSerializer::class)
    val yearMonth: YearMonth,
    var workedTime: Long = 0L,
    var calculatedTime: Long = 0L,
    val correctionTime: Long = 0L
)
