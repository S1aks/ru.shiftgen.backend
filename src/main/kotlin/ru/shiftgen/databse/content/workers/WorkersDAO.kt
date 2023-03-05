package ru.shiftgen.databse.content.workers

import org.jetbrains.exposed.sql.ResultRow

interface WorkersDAO {
    fun ResultRow.toWorkerDTO() = WorkerDTO(
        id = this[Workers.id],
        personnelNumber = this[Workers.personnelNumber],
        userId = this[Workers.userId],
        firstName = this[Workers.firstName],
        lastName = this[Workers.lastName],
        patronymic = this[Workers.patronymic],
        accessToEvents = this[Workers.accessToEvents]
    )


    suspend fun insertWorker(worker: WorkerDTO): Boolean
    suspend fun updateWorker(worker: WorkerDTO): Boolean
    suspend fun getWorker(id: Int): WorkerDTO?
    suspend fun deleteWorker(id: Int): Boolean
}