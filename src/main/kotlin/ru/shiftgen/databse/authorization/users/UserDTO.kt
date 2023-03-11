package ru.shiftgen.databse.authorization.users

import ru.shiftgen.databse.authorization.groups.Groups

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
    val workerId: Int? = null,
    val structureId: Int? = null
)