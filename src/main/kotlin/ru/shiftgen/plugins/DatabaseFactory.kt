package ru.shiftgen.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiftgen.databse.authorization.groups.Groups
import ru.shiftgen.databse.authorization.tokens.Tokens
import ru.shiftgen.databse.authorization.users.UserDTO
import ru.shiftgen.databse.authorization.users.Users
import ru.shiftgen.databse.content.directions.Directions
import ru.shiftgen.databse.content.events.Events
import ru.shiftgen.databse.content.shifts.Shifts
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.databse.content.time_blocks.TimeBlocks
import ru.shiftgen.databse.content.timesheets.TimeSheets
import ru.shiftgen.databse.content.workers.Workers

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
            SchemaUtils.create(Tokens)
            SchemaUtils.create(Users)
            SchemaUtils.create(Directions)
            SchemaUtils.create(Events)
            SchemaUtils.create(Shifts)
            SchemaUtils.create(Structures)
            SchemaUtils.create(TimeBlocks)
            SchemaUtils.create(TimeSheets)
            SchemaUtils.create(Workers)
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