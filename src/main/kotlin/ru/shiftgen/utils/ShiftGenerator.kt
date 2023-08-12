package ru.shiftgen.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.databse.content.shifts.Shifts
import ru.shiftgen.databse.content.structures.StructureDTO
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.databse.content.timesheets.TimeSheetDTO
import ru.shiftgen.databse.content.timesheets.TimeSheets
import ru.shiftgen.databse.content.workers.WorkerDTO
import ru.shiftgen.databse.content.workers.Workers
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit

object ShiftGenerator {

    private fun getBusyOrRestWorkersIdsOnTime(
        shifts: List<ShiftDTO>, time: LocalDateTime, restHours: Int
    ): List<Int> = shifts
        .filter { it.workerId != null }
        .filter { shift ->
            time >= shift.startTime && time <= shift.endTimeWithRestCorrection(restHours.hoursToMillis)
        }
        .map { it.workerId ?: throw RuntimeException("Error map workers id`s in getBusyOrRest function") }

    private fun getNumberOfNightsInShift(shift: ShiftDTO, nightStartHour: Int, nightEndHour: Int): Int {
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

    private fun getWorkersIdsWhoCannotWorkThatNight(
        shifts: List<ShiftDTO>,
        currentShift: ShiftDTO,
        restHours: Int,
        allowedConsecutiveNights: Int,
        nightStartHour: Int,
        nightEndHour: Int
    ): List<Int> = shifts
        .filter { it.workerId != null }
        .filter { shift ->
            shift.startTime >= currentShift.startTime.minusDays(allowedConsecutiveNights.toLong())
                .toLocalDate().atStartOfDay().plusHours(nightEndHour.toLong()) &&
                    shift.endTimeWithRestCorrection(restHours.hoursToMillis) < currentShift.startTime &&
                    getNumberOfNightsInShift(shift, nightStartHour, nightEndHour) > 0
        } // Оставляем те смены, были не ранее allowedConsecutiveNights ночей назад,
        // которые закончатся за restHours до начала текущей смены и затрагивали ночь
        .groupBy({ it.workerId!! }, { getNumberOfNightsInShift(it, nightStartHour, nightEndHour) })
        .mapValues { it.value.sum() } // Сгруппировать по Id и посчитать количество ночей у каждого
        .filter { it.value == allowedConsecutiveNights } // Выбрать тех у кого уже разрешенное количества ночей
        .map { it.key } // Преобразовать в список Id

    private fun List<WorkerDTO>.getFreeWorkersForShift(
        shifts: List<ShiftDTO>, shift: ShiftDTO, restHours: Int
    ): List<WorkerDTO> =
        this // Берём список рабочих,
            // Отсеиваем тех, кто не имеет доступа к направлению
            .filter { it.accessToDirections?.contains(shift.directionId) ?: false }
            // Отсеиваем тех кто занят или на отдыхе после смены
            .filter {
                it.id !in getBusyOrRestWorkersIdsOnTime(
                    shifts,
                    shift.startTime,
                    restHours
                )
            }

    private suspend fun List<TimeSheetDTO>.updateTimeSheet(
        shift: ShiftDTO,
        timeNow: LocalDateTime
    ) {
        val workTime = shift.duration - shift.restDuration
        find { it.workerId == shift.workerId }?.let { timeSheet ->
            timeSheet.calculatedTime = timeSheet.calculatedTime.plus(workTime)
            if (timeNow > shift.endTime()) {
                timeSheet.workedTime = timeSheet.workedTime.plus(workTime)
            }
            TimeSheets.updateTimeSheet(timeSheet)
        }
    }

    internal suspend fun arrangeTheWorkers(
        structureId: Int,
        yearMonth: YearMonth
    ): Unit = withContext(Dispatchers.Default) {
        val structure: StructureDTO =
            Structures.getStructure(structureId) ?: throw RuntimeException("Error get structure data.")
        val currentPeriodShifts = Shifts.getShifts(structure.id, yearMonth)
        val yearMonthPrev: YearMonth = yearMonth.minusMonths(1)
        val prevPeriodShifts = Shifts.getShifts(structure.id, yearMonthPrev).filter { shift ->
            shift.endTimeWithRestCorrection(structure.restHours.hoursToMillis).toYearMonth() == yearMonth
        }
        val shifts = (prevPeriodShifts + currentPeriodShifts).toMutableList()
        val workers = Workers.getWorkers(structure.id)
        val timeSheets = TimeSheets.getTimeSheetsInYearMonth(structure.id, yearMonth).toMutableList()
        val timeNow = LocalDateTime.now()
        shifts.forEach { shift ->
            if (shift.startTime > timeNow) {
                shift.workerId = null
            }
        }
        workers.forEach { worker -> // Проверяем, что для каждого рабочего есть табель учета времени
            if (timeSheets.firstOrNull { it.workerId == worker.id } == null) {
                TimeSheets.insertTimeSheet(structureId, TimeSheetDTO(0, worker.id, yearMonth))?.let { id ->
                    timeSheets.add(TimeSheetDTO(id, worker.id, yearMonth))
                }
                    ?: throw RuntimeException("Error insert timesheet for workerId = ${worker.id} in year-month $yearMonth")
            }
        }
        timeSheets.forEach { timeSheet ->
            timeSheet.workedTime = 0
            timeSheet.calculatedTime = 0
        }
        shifts.filter { it.startTime.toYearMonth() == yearMonth }
            .forEach { shift ->
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
                            timeSheets.find { it.workerId == worker.id }!!.calculatedTime
                        }?.id
                    if (shift.workerId != null) { // Если рабочий найден
                        Shifts.updateShift(shift) // Сохраняем его в таблицу смен
                        timeSheets.updateTimeSheet(shift, timeNow)  // и обновляем табель
                    }
                } else { // если рабочий есть, то просто обновляем табель
                    timeSheets.updateTimeSheet(shift, timeNow)
                }
            }
    }
}
