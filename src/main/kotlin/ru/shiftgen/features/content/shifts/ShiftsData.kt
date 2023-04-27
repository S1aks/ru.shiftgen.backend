package ru.shiftgen.features.content.shifts

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.enums.Periodicity
import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.utils.LocalDateTimeSerializer
import ru.shiftgen.utils.YearMonthSerializer
import java.time.LocalDateTime
import java.time.YearMonth

@Serializable
data class ShiftRequest(
    val id: Int,
    val name: String,
    @Serializable(with = YearMonthSerializer::class)
    val yearMonth: YearMonth,
    val periodicity: Periodicity,
    val workerId: Int?,
    val structureId: Int,
    val directionId: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTime: LocalDateTime,
    val timeBlocksIds: List<Int>
)

@Serializable
data class ShiftsRequest(
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