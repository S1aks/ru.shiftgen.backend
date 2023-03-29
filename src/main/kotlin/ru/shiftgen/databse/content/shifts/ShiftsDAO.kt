package ru.shiftgen.databse.content.shifts

import org.jetbrains.exposed.sql.ResultRow
import ru.shiftgen.databse.content.enums.Periodicity
import java.time.LocalDateTime
import java.time.YearMonth

interface ShiftsDAO {
    fun ResultRow.toShiftDTO() = ShiftDTO(
        id = this[Shifts.id],
        name = this[Shifts.name],
        periodYearMonth = YearMonth.parse(this[Shifts.periodYearMonth]),
        periodicity = Periodicity.values()[this[Shifts.periodicity]],
        workerId = this[Shifts.workerId],
        structureId = this[Shifts.structureId],
        directionId = this[Shifts.directionId],
        startTime = LocalDateTime.parse(this[Shifts.startTime]),
        timeBlocksIds = this[Shifts.timeBlocksIds].split(",").map { it.toInt() }
    )

    suspend fun insertShift(shift: ShiftDTO): Boolean
    suspend fun updateShift(shift: ShiftDTO): Boolean
    suspend fun getShift(id: Int): ShiftDTO?
    suspend fun getShifts(structureId: Int, periodYearMonth: YearMonth): List<ShiftDTO>
    suspend fun deleteShift(id: Int): Boolean
}