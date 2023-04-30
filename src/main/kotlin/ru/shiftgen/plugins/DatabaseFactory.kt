package ru.shiftgen.plugins

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiftgen.databse.authorization.groups.Groups
import ru.shiftgen.databse.authorization.tokens.Tokens
import ru.shiftgen.databse.authorization.users.UserDTO
import ru.shiftgen.databse.authorization.users.Users
import ru.shiftgen.databse.content.directions.Directions
import ru.shiftgen.databse.content.shifts.Shifts
import ru.shiftgen.databse.content.structures.StructureDTO
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.databse.content.time_blocks.TimeBlocks
import ru.shiftgen.databse.content.timesheets.TimeSheets
import ru.shiftgen.databse.content.workers.Workers

object DatabaseFactory {
    private const val dbUser = "postgres"

    //    private const val dbPassword = "d24013" // local
    private const val dbPassword = "D!24013555" // remote
    private const val driverClassName = "org.postgresql.Driver"
    private const val jdbcURL = "jdbc:postgresql://127.0.0.1:5432/shiftgen_db"
    private const val adminLogin = "adm1n"
    private const val adminPassword = "D43_7fG+3m/"
    private const val adminEmail = "admin@shiftgen.ru"
    private const val adminFirstName = "Administrator"
    private const val adminLastName = ""

    fun init() {
        val database = Database.connect(jdbcURL, driverClassName, dbUser, dbPassword)
        transaction(database) {
            SchemaUtils.create(Tokens)
            SchemaUtils.create(Users)
            SchemaUtils.create(Directions)
            SchemaUtils.create(Shifts)
            SchemaUtils.create(Structures)
            SchemaUtils.create(TimeBlocks)
            SchemaUtils.create(TimeSheets)
            SchemaUtils.create(Workers)
        }
        CoroutineScope(Job()).launch { initAdminAccount() }
        CoroutineScope(Job()).launch { createTestData() }
    }

    private suspend fun initAdminAccount() = dbQuery {
        if (!Users.ifUserExist(adminLogin)) {
            val superUser = UserDTO(
                login = adminLogin,
                password = adminPassword,
                email = adminEmail,
                firstName = adminFirstName,
                lastName = adminLastName,
                group = Groups.ADMIN
            )
            if (!Users.insertUser(superUser)) throw Exception("Error initialize admin account!")
        }
    }

    private suspend fun createTestData() = dbQuery {
        if (Structures.getStructure(1) == null) {
            Structures.insertStructure(StructureDTO(1, "Тестовый раздел", "Тестовая структура для пробных запросов."))
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}