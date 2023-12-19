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
        accessToDirections = this[Workers.accessToDirections]?.split(",")?.map { it.toInt() },
        fired = this[Workers.fired]
    )

    fun ResultRow.workerStructureId() = this[Workers.structureId]

    suspend fun insertWorker(structureId: Int, worker: WorkerDTO): Boolean
    suspend fun updateWorker(worker: WorkerDTO): Boolean
    suspend fun getWorker(id: Int): WorkerDTO?
    suspend fun getWorkers(structureId: Int): List<WorkerDTO>
    suspend fun getWorkerStructureId(id: Int): Int?
    suspend fun deleteWorker(id: Int): Boolean
}