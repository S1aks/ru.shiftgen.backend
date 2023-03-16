package ru.shiftgen.databse.content.timesheets

import java.time.YearMonth

data class TimeSheetDTO(
    val id: Int = 0,
    val workerId: Int,
    val structureId: Int,
    val periodYearMonth: YearMonth,
    val workedTime: Long = 0L,
    val calculatedTime: Long = 0L,
    val correctionTime: Long = 0L
)
