package ru.shiftgen.databse.authorization.users

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import ru.shiftgen.databse.authorization.tokens.Tokens
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.databse.content.workers.Workers
import ru.shiftgen.plugins.DatabaseFactory.dbQuery
import ru.shiftgen.plugins.PasswordEncryptor
import java.time.LocalDateTime

object Users : Table(), UsersDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val login = varchar("login", 25).uniqueIndex()
    internal val password = varchar("password", 40)
    internal val email = varchar("email", 30)
    internal val phone = varchar("phone", 30)
    internal val firstName = varchar("first_name", 30)
    internal val lastName = varchar("last_name", 30)
    internal val patronymic = varchar("patronymic", 30)
    internal val group = integer("group")
    internal val workerId = reference("worker_id", Workers.id).nullable()
    internal val structureId = reference("structure_id", Structures.id).nullable()
    internal val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() } // todo For statistic
    override val primaryKey = PrimaryKey(login)

    override suspend fun ifUserExist(login: String): Boolean = dbQuery {
        Users.select { Users.login eq login }.singleOrNull() != null
    }

    override suspend fun insertUser(user: UserDTO): Boolean = dbQuery {
        Users.insert {
            it[login] = user.login
            it[password] = PasswordEncryptor.hash(user.password)
            it[email] = user.email
            it[phone] = user.phone
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[patronymic] = user.patronymic
            it[group] = user.group.ordinal
            it[workerId] = user.workerId
            it[structureId] = user.structureId
        }.insertedCount > 0
    }

    override suspend fun updateUser(user: UserDTO): Boolean = dbQuery {
        Users.update({ id eq user.id }) {
            it[password] = PasswordEncryptor.hash(user.password)
            it[email] = user.email
            it[phone] = user.phone
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[patronymic] = user.patronymic
            it[group] = user.group.ordinal
            it[workerId] = user.workerId
            it[structureId] = user.structureId
        } > 0
    }

    override suspend fun getUserById(id: Int): UserDTO? = dbQuery {
        Users.select { Users.id eq id }.singleOrNull()?.toUserDTO()
    }

    override suspend fun getUserByLogin(login: String): UserDTO? = dbQuery {
        Users.select { Users.login eq login }.singleOrNull()?.toUserDTO()
    }

    override suspend fun getUserLogin(id: Int): String? = dbQuery {
        Users.select { Users.id eq id }.singleOrNull()?.let { it[login] }
    }

    override suspend fun getUserId(login: String): Int? = dbQuery {
        Users.select { Users.login eq login }.singleOrNull()?.let { it[id] }
    }

    override suspend fun getUsersByStructure(structureId: Int): List<UserDTO> = dbQuery {
        Users.select { Users.structureId eq structureId }.map { it.toUserDTO() }
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        getUserLogin(id)?.let { login ->
            if (Tokens.deleteToken(login)) {
                Users.deleteWhere { Users.id eq id } > 0
            } else {
                false
            }
        } ?: false
    }
}