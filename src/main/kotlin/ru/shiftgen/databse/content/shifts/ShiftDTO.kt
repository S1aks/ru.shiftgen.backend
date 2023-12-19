package ru.shiftgen.databse.content.shifts

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.enums.Action
import ru.shiftgen.databse.content.enums.Periodicity
import ru.shiftgen.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class ShiftDTO(
    val id: Int,
    val name: String,
    val periodicity: Periodicity,
    var workerId: Int?,
    val manualWorkerSelection: Boolean,
    val directionId: Int,
    val action: Action,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTime: LocalDateTime,
    val duration: Long,
    val restDuration: Long
)
