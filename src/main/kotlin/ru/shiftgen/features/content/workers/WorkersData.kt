package ru.shiftgen.features.content.workers

import kotlinx.serialization.Serializable
import ru.shiftgen.databse.content.workers.WorkerDTO

@Serializable
data class WorkerReceive(
    val id: Int,
    val personnelNumber: Int?,
    val userId: Int?,
    val structureId: Int,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val accessToDirections: List<Int>?
)

@Serializable
data class WorkerResponse(
    val worker: WorkerDTO
)

@Serializable
data class WorkersResponse(
    val list: List<WorkerDTO>
)