package ru.shiftgen.databse.authorization.users

import ru.shiftgen.databse.authorization.groups.Groups

data class UserDTO(
    val id: Int = 0,
    val login: String,
    val password: String,
    val email: String? = null,
    val phone: String? = null,
    val firstName: String,
    val lastName: String,
    val patronymic: String? = null,
    val group: Groups,
    val workerId: Int? = null,
    val structureId: Int? = null
)