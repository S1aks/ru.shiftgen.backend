package ru.shiftgen.databse.access.users

import ru.shiftgen.databse.access.groups.Groups

data class UserDTO(
    val id: Int = 0,
    val login: String,
    val password: String,
    val email: String?,
    val phone: String?,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val accessGroup: Groups,
    val workerId: Int?,
    val structureId: Int?
)