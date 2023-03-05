package ru.shiftgen.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiftgen.databse.access.groups.Groups
import ru.shiftgen.databse.access.tokens.Tokens
import ru.shiftgen.databse.access.users.UserDTO
import ru.shiftgen.databse.access.users.Users

object DatabaseFactory {
    private const val dbUser = "postgres"
    private const val dbPassword = "d24013"
    private const val driverClassName = "org.postgresql.Driver"
    private const val jdbcURL = "jdbc:postgresql://localhost:5432/shiftgen"
    private const val adminLogin = "adm1n"
    private const val adminPassword = "D43_7fG+3m/"
    private const val adminEmail = "admin@shiftgen.ru"
    private const val adminFirstName = "Administrator"
    private const val adminLastName = ""

    fun init() {
        val database = Database.connect(jdbcURL, driverClassName, dbUser, dbPassword)
        transaction(database) {
            SchemaUtils.create(Users)
            SchemaUtils.create(Tokens)
        }
        runBlocking {
            initAdminAccount()
        }
    }

    private suspend fun initAdminAccount() = dbQuery {
        if (!Users.ifUserExist(adminLogin)) {
            val superUser = UserDTO(
                login = adminLogin,
                password = adminPassword,
                email = adminEmail,
                phone = null,
                firstName = adminFirstName,
                lastName = adminLastName,
                patronymic = null,
                accessGroup = Groups.ADMIN,
                structureId = null,
                workerId = null
            )
            if (!Users.insertUser(superUser)) throw Exception("Error initialize admin account!")
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}