package ru.shiftgen.databse.access.users

import org.jetbrains.exposed.sql.ResultRow

interface UsersDAO {
    fun ResultRow.toUserDTO() = UserDTO(
        id = this[Users.id],
        login = this[Users.login],
        password = this[Users.password],
        email = this[Users.email],
        phone = this[Users.phone],
        firstName = this[Users.firstName],
        lastName = this[Users.lastName],
        patronymic = this[Users.patronymic],
        accessGroupId = this[Users.accessGroupId],
        workerId = this[Users.workerId],
        structureId = this[Users.structureId]
    )

    suspend fun insertUser(user: UserDTO): Boolean
    suspend fun updateUser(user: UserDTO): Boolean
    suspend fun ifUserExist(login: String): Boolean
    suspend fun getUserById(id: Int): UserDTO?
    suspend fun getUserByLogin(login: String): UserDTO?
    suspend fun getUserLogin(id: Int): String?
    suspend fun getUserId(login: String): Int?
    suspend fun deleteUser(id: Int): Boolean
}