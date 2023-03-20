package ru.shiftgen.features.content.shifts

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.enums.Periodicity
import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.utils.YearMonthSerializer
import java.time.YearMonth

@Serializable
data class ShiftReceive(
    val id: Int,
    val name: String,
    @Serializable(with = YearMonthSerializer::class)
    val periodYearMonth: YearMonth,
    val periodicity: Periodicity,
    val workerId: Int?,
    val structureId: Int,
    val startTime: Long,
    val eventId: Int
)

@Serializable
data class ShiftsReceive(
    @Serializable(with = YearMonthSerializer::class)
    val periodYearMonth: YearMonth
)

@Serializable
data class ShiftResponse(
    val event: ShiftDTO
)

@Serializable
data class ShiftsResponse(
    val list: List<ShiftDTO>
)