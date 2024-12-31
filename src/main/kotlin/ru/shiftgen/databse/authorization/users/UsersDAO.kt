package ru.shiftgen.databse.authorization.users

import org.jetbrains.exposed.sql.ResultRow
import ru.shiftgen.databse.authorization.groups.Groups

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
        group = Groups.values()[this[Users.group]],
        workerId = this[Users.workerId],
        structureId = this[Users.structureId]
    )

    suspend fun ifUserExist(login: String): Boolean
    suspend fun insertUser(user: UserDTO): Boolean
    suspend fun updateUser(user: UserDTO): Boolean
    suspend fun getUserById(id: Int): UserDTO?
    suspend fun getUserByLogin(login: String): UserDTO?
    suspend fun getUserLogin(id: Int): String?
    suspend fun getUserId(login: String): Int?
    suspend fun getUsersByStructure(structureId: Int): List<UserDTO>
    suspend fun deleteUser(id: Int): Boolean
}