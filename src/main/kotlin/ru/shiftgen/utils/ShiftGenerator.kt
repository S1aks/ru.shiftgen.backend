package ru.shiftgen.utils

import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.databse.content.shifts.Shifts
import ru.shiftgen.databse.content.structures.StructureDTO
import ru.shiftgen.databse.content.timesheets.TimeSheets
import ru.shiftgen.databse.content.workers.WorkerDTO
import ru.shiftgen.databse.content.workers.Workers
import ru.shiftgen.plugins.DatabaseFactory.dbQuery
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class ShiftGenerator {

    private suspend fun ShiftDTO.endTimeWithRestCorrection(correctTime: Long): LocalDateTime =
        this.startTime.plus(this.duration + correctTime, ChronoUnit.MILLIS)

    private suspend fun ShiftDTO.endTime(): LocalDateTime =
        this.startTime.plus(this.duration, ChronoUnit.MILLIS)

    private suspend fun getBusyOrRestWorkersIdsOnTime(
        shifts: List<ShiftDTO>, yearMonth: YearMonth, time: LocalDateTime, restHours: Int
    ): List<Int?> = shifts
        .filter { it.workerId != null }
        .filter { shift ->
            time >= shift.startTime && time <= shift.endTimeWithRestCorrection(restHours.hoursToMillis)
        }
        .filter { it.yearMonth == yearMonth }
        .map { it.workerId }

//    private suspend fun isShiftCrossNight(shift: ShiftDTO, nightStartHour: Int, nightEndHour: Int): Boolean {
//        val shiftStart = shift.startTime
//        val shiftEnd = shift.endTime()
//        val nightStart = shiftStart.withHour(nightStartHour)
//        val nightEnd = shiftStart.withHour(nightEndHour)
//        return if (nightStartHour in 18..23) {
//            shiftStart < nightEnd && shiftEnd > nightEnd.minusDays(1) ||
//                    shiftStart < nightEnd.plusDays(1) && shiftEnd > nightStart
//        } else {
//            shiftStart < nightEnd && shiftEnd > nightStart ||
//                    shiftStart < nightEnd.plusDays(1) && shiftEnd > nightStart.plusDays(1)
//        }
//    }

    private suspend fun getNumberOfNightsInShift(shift: ShiftDTO, nightStartHour: Int, nightEndHour: Int): Int {
        val shiftStart = shift.startTime
        val shiftEnd = shift.endTime()
        var numberOfNights = 0
        if (shiftStart.hour < nightEndHour) numberOfNights++
        if (nightStartHour >= 18 && shiftEnd.hour > nightStartHour) numberOfNights++
        numberOfNights += ChronoUnit.DAYS.between(
            shiftStart.toLocalDate().atStartOfDay(), shiftEnd.toLocalDate().atStartOfDay()
        ).toInt()
        return numberOfNights
    }

    private suspend fun getWorkersIdsWhoCannotWorkThatNight(
        shifts: List<ShiftDTO>,
        currentShift: ShiftDTO,
        restHours: Int,
        allowedConsecutiveNights: Int,
        nightStartHour: Int,
        nightEndHour: Int
    ): List<Int?> = shifts
        .filter { it.workerId != null }
        .filter { shift ->
            shift.startTime >= currentShift.startTime.minusDays(allowedConsecutiveNights.toLong())
                .toLocalDate().atStartOfDay().plusHours(nightEndHour.toLong()) &&
                    shift.endTimeWithRestCorrection(restHours.hoursToMillis) < currentShift.startTime &&
                    getNumberOfNightsInShift(shift, nightStartHour, nightEndHour) > 0
        } // Оставляем те смены, были не ранее allowedConsecutiveNights ночей назад,
        // которые закончатся за restHours до начала текущей смены и затрагивали ночь
        .groupBy({ it.workerId }, { getNumberOfNightsInShift(it, nightStartHour, nightEndHour) })
        .mapValues { it.value.sum() } // Сгруппировать по Id и посчитать количество ночей у каждого
        .filter { it.value == allowedConsecutiveNights } // Отсеять тех у кого есть 6 ночей
        .map { it.key } // Преобразовать в список Id

    private suspend fun List<WorkerDTO>.getFreeWorkersForShift(
        shifts: List<ShiftDTO>, shift: ShiftDTO, restHours: Int
    ): List<WorkerDTO> =
        this // Берём список рабочих,
            // Отсеиваем тех, кто не имеет доступа к направлению
            .filter { it.accessToDirections?.contains(shift.directionId) ?: false }
            // Отсеиваем тех кто занят или на отдыхе после смены
            .filter {
                it.id !in getBusyOrRestWorkersIdsOnTime(shifts, shift.yearMonth, shift.startTime, restHours)
            }

    suspend fun fillShiftsListWithWorkers(
        structure: StructureDTO,
        yearMonth: YearMonth
    ): List<ShiftDTO> = dbQuery {
        val prevPeriodShifts = Shifts.getShifts(structure.id, yearMonth.minusMonths(1))
        val currentPeriodShifts = Shifts.getShifts(structure.id, yearMonth)
        val shifts = prevPeriodShifts + currentPeriodShifts
        val workers = Workers.getWorkers(structure.id)
        val timeSheets = TimeSheets.getTimeSheetsInYearMonth(structure.id, yearMonth)
        shifts.forEach { shift ->
            if (shift.workerId == null) {   // Если рабочий не назначен
                shift.workerId = workers
                    // Берем список свободных рабочих на данное направление
                    .getFreeWorkersForShift(shifts, shift, structure.restHours)
                    .filter {
                        if (structure.allowedConsecutiveNights > 0) {
                            it.id !in getWorkersIdsWhoCannotWorkThatNight(
                                shifts,
                                shift,
                                structure.restHours,
                                structure.allowedConsecutiveNights,
                                structure.nightStartHour,
                                structure.nightEndHour
                            )
                        } else {
                            true
                        }
                    } // Отсеиваем по условию работы нескольких ночей (allowedConsecutiveNights) ночей подряд
                    // Из оставшихся выбираем того рабочего, у которого меньше всего отработано часов
                    .minByOrNull { worker ->
                        timeSheets.first { it.workerId == worker.id }.workedTime
                    }?.id
                if (shift.workerId != null) { // Если рабочий найден
                    workers.find { it.id == shift.workerId }?.let { worker ->
                        // Рассчитываем рабочее время в поездке
                        val workTime = shift.duration - shift.restDuration
                        val timeSheet = timeSheets.first { it.workerId == worker.id }
                        timeSheet.calculatedTime = timeSheet.calculatedTime.plus(workTime)
                        if (LocalDateTime.now() > shift.endTime()) {
                            timeSheet.workedTime = timeSheet.workedTime.plus(workTime)
                        }
                        TimeSheets.updateTimeSheet(timeSheet)
                    } //  то заполняем поля табеля и обновляем в таблице
                }
            }
        }
        shifts
    }

}