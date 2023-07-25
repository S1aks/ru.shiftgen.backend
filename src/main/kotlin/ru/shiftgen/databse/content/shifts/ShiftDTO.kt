package ru.shiftgen.databse.content.shifts

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.enums.Action
import ru.shiftgen.databse.content.enums.Periodicity
import ru.shiftgen.utils.LocalDateTimeSerializer
import ru.shiftgen.utils.YearMonthSerializer
import java.time.LocalDateTime
import java.time.YearMonth

@Serializable
data class ShiftDTO(
    val id: Int = 0,
    val name: String,
    @Serializable(with = YearMonthSerializer::class)
    val yearMonth: YearMonth,
    val periodicity: Periodicity,
    var workerId: Int?,
    val directionId: Int,
    val action: Action,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTime: LocalDateTime,
    val duration: Long,
    val restDuration: Long
)
