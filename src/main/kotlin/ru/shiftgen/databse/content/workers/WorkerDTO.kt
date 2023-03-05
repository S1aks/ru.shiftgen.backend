package ru.shiftgen.databse.content.workers

data class WorkerDTO(
    val id: Int,
    val personnelNumber: Int?,
    val userId: Int?,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val accessToEvents: String?
)
