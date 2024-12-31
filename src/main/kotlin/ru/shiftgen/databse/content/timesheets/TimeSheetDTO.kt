package ru.shiftgen.databse.content.timesheets

import kotlinx.serialization.Serializable
import ru.shiftgen.utils.YearMonthSerializer
import java.time.YearMonth

@Serializable
data class TimeSheetDTO(
    val id: Int,
    val workerId: Int,
    @Serializable(with = YearMonthSerializer::class)
    val yearMonth: YearMonth,
    var workedTime: Long,
    var calculatedTime: Long,
    val correctionTime: Long
)
