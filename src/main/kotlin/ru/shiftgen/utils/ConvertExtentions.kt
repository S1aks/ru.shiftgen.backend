package ru.shiftgen.utils

import java.time.LocalDateTime
import java.time.YearMonth
import java.util.concurrent.TimeUnit

val Int.hoursToMillis: Long // Экстеншн для перевода интового значения часов в millis
    get() = TimeUnit.HOURS.toMillis(this.toLong())

fun LocalDateTime.toYearMonth(): YearMonth = YearMonth.of(year, month)