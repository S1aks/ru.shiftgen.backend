package ru.shiftgen.features.content.shifts

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.enums.Action
import ru.shiftgen.databse.content.enums.Periodicity
import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.utils.LocalDateTimeSerializer
import ru.shiftgen.utils.YearMonthSerializer
import java.time.LocalDateTime
import java.time.YearMonth

@Serializable
data class ShiftReceive(
    val id: Int,
    val name: String,
    val periodicity: Periodicity,
    val workerId: Int?,
    val directionId: Int,
    val action: Action,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTime: LocalDateTime,
    val duration: Long,
    val restDuration: Long
)

@Serializable
data class ShiftsReceive(
    @Serializable(with = YearMonthSerializer::class)
    val yearMonth: YearMonth
)

@Serializable
data class ShiftResponse(
    val shift: ShiftDTO
)

@Serializable
data class ShiftsResponse(
    val list: List<ShiftDTO>
)

@Serializable
data class YearMonthsResponse(
    val list: List<String>
)