package ru.shiftgen.utils

import java.time.LocalDateTime
import java.time.YearMonth
import java.util.concurrent.TimeUnit

val Int.hoursToMillis: Long // Экстеншн для перевода интового значения часов в millis
    get() = TimeUnit.HOURS.toMillis(this.toLong())

fun LocalDateTime.toYearMonth(): YearMonth = YearMonth.of(year, month)

fun Int.toEvenInt(): Int = if (this % 2 == 0) this else this + 1

fun Int.toOddInt(): Int = if (this % 2 == 0) this + 1 else this