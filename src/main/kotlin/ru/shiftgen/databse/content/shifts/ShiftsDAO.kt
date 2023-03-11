package ru.shiftgen.databse.content.shifts

import org.jetbrains.exposed.sql.ResultRow
import ru.shiftgen.databse.content.enums.Periodicity

interface ShiftsDAO {
    fun ResultRow.toShiftDTO() = ShiftDTO(
        id = this[Shifts.id],
        name = this[Shifts.name],
        periodicity = Periodicity.values()[this[Shifts.periodicity]],
        workerId = this[Shifts.workerId],
        startTime = this[Shifts.startTime],
        eventId = this[Shifts.eventId]
    )

    suspend fun insertShift(shift: ShiftDTO): Boolean
    suspend fun updateShift(shift: ShiftDTO): Boolean
    suspend fun getShift(id: Int): ShiftDTO?
    suspend fun deleteShift(id: Int): Boolean
}