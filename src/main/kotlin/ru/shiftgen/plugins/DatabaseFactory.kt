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
import ru.shiftgen.databse.content.timesheets.TimeSheets
import ru.shiftgen.databse.content.workers.Workers

object DatabaseFactory {
    private const val DB_USER = "postgres"
    //    private const val DB_PASSWORD = "d24013" // local
    private const val DB_PASSWORD = "D!24013555" // remote
    private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
    private const val JDBC_URL = "jdbc:postgresql://127.0.0.1:5432/shiftgen_db"
    private const val ADMIN_LOGIN = "adm1n"
    private const val ADMIN_PASSWORD = "D43_7fG+3m/"
    private const val ADMIN_EMAIL = "admin@shiftgen.ru"
    private const val ADMIN_FIRST_NAME = "Administrator"
    private const val ADMIN_LAST_NAME = ""

    fun init() {
        val database = Database.connect(JDBC_URL, DRIVER_CLASS_NAME, DB_USER, DB_PASSWORD)
        transaction(database) {
            SchemaUtils.createDatabase()
            SchemaUtils.create(Tokens, Users, Directions, Shifts, Structures, TimeSheets, Workers)
        }
        CoroutineScope(Job()).launch { initAdminAccount() }
        CoroutineScope(Job()).launch { createTestData() }
    }

    private suspend fun initAdminAccount() = dbQuery {
        if (!Users.ifUserExist(ADMIN_LOGIN)) {
            val superUser = UserDTO(
                id = 0,
                login = ADMIN_LOGIN,
                password = ADMIN_PASSWORD,
                email = ADMIN_EMAIL,
                phone = "",
                firstName = ADMIN_FIRST_NAME,
                lastName = ADMIN_LAST_NAME,
                patronymic = "",
                group = Groups.ADMIN,
                workerId = null,
                structureId = null
            )
            if (!Users.insertUser(superUser)) throw Exception("Error initialize admin account!")
        }
    }

    private suspend fun createTestData() = dbQuery {
        if (Structures.getStructure(1) == null) {
            Structures.insertStructure(
                StructureDTO(
                    id = 1,
                    name = "Тест",
                    description = "",
                    restHours = 0,
                    allowedConsecutiveNights = 0,
                    nightStartHour = 0,
                    nightEndHour = 6,
                    dispatcherPin = "55555"
                )
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}