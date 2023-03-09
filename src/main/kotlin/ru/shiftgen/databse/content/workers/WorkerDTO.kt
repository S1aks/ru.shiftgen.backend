package ru.shiftgen.databse.content.workers

data class WorkerDTO(
    val id: Int = 0,
    val personnelNumber: Int?,
    val userId: Int?,
    val structureId: Int?,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val accessToDirections: List<Int>?
)
