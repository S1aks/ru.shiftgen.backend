package ru.shiftgen.databse.content.shifts

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.enums.Periodicity
import ru.shiftgen.utils.YearMonthSerializer
import java.time.YearMonth

@Serializable
data class ShiftDTO(
    val id: Int = 0,
    val name: String,
    @Serializable(with = YearMonthSerializer::class)
    val periodYearMonth: YearMonth,
    val periodicity: Periodicity,
    val workerId: Int?,
    val structureId: Int,
    val startTime: Long,
    val eventId: Int
)
