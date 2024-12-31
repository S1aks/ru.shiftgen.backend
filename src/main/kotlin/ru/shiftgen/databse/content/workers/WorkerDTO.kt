package ru.shiftgen.databse.content.workers

import kotlinx.serialization.Serializable

@Serializable
data class WorkerDTO(
    val id: Int,
    val personnelNumber: Int?,
    val userId: Int?,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val accessToDirections: List<Int>?,
    val fired: Boolean
)
