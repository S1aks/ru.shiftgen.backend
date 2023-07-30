package ru.shiftgen.databse.content.shifts

import org.jetbrains.exposed.sql.ResultRow
import ru.shiftgen.databse.content.enums.Action
import ru.shiftgen.databse.content.enums.Periodicity
import java.time.LocalDateTime
import java.time.YearMonth

interface ShiftsDAO {
    fun ResultRow.toShiftDTO() = ShiftDTO(
        id = this[Shifts.id],
        name = this[Shifts.name],
        periodicity = Periodicity.values()[this[Shifts.periodicity]],
        workerId = this[Shifts.workerId],
        directionId = this[Shifts.directionId],
        action = Action.values()[this[Shifts.action]],
        startTime = LocalDateTime.parse(this[Shifts.startTime]),
        duration = this[Shifts.duration],
        restDuration = this[Shifts.restDuration]
    )

    fun ResultRow.structureId() = this[Shifts.structureId]

    suspend fun insertShift(structureId: Int, shift: ShiftDTO): Boolean
    suspend fun updateShift(structureId: Int, shift: ShiftDTO): Boolean
    suspend fun getShift(id: Int): ShiftDTO?
    suspend fun getShiftStructureId(id: Int): Int?
    suspend fun getShifts(structureId: Int, yearMonth: YearMonth): List<ShiftDTO>
    suspend fun getYearMonths(structureId: Int): List<String>
    suspend fun deleteShift(id: Int): Boolean
}