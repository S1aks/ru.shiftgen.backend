package ru.shiftgen.utils

import ru.shiftgen.databse.content.shifts.ShiftDTO
import ru.shiftgen.features.content.shifts.ShiftReceive
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

val Int.hoursToMillis: Long // Экстеншн для перевода интового значения часов в millis
    get() = TimeUnit.HOURS.toMillis(this.toLong())

fun LocalDateTime.toYearMonth(): YearMonth = YearMonth.of(year, month)

fun ShiftDTO.endTimeWithRestCorrection(correctTime: Long): LocalDateTime =
    this.startTime.plus(this.duration + correctTime, ChronoUnit.MILLIS)

fun ShiftDTO.endTime(): LocalDateTime = this.startTime.plus(this.duration, ChronoUnit.MILLIS)

fun ShiftReceive.endTime(): LocalDateTime = this.startTime.plus(this.duration, ChronoUnit.MILLIS)

fun Int.toEvenInt(): Int = if (this % 2 == 0) this else this + 1

fun Int.toOddInt(): Int = if (this % 2 == 0) this + 1 else this