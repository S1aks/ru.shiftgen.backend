package ru.shiftgen.databse.content.timesheet

import java.time.YearMonth

data class TimeSheetDTO(
    val id: Int = 0,
    val workerId: Int,
    val periodYearMonth: YearMonth,
    val workedTime: Long,
    val calculatedTime: Long,
    val correctionTime: Long
)
