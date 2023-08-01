package ru.shiftgen.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Unit.andArrangeTheWorkers(structureId: Int): Unit = withContext(Dispatchers.Default) {
    ShiftGenerator.arrangeTheWorkers(structureId)
}