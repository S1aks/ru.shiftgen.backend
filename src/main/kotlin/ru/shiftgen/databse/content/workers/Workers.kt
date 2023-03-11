package ru.shiftgen.databse.content.workers

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shiftgen.databse.authorization.users.Users
import ru.shiftgen.databse.content.structures.Structures
import ru.shiftgen.plugins.DatabaseFactory.dbQuery

object Workers : Table(), WorkersDAO {
    internal val id = integer("id").uniqueIndex().autoIncrement()
    internal val personnelNumber = integer("personnel_number").nullable()
    internal val userId = reference("user_id", Users.id).nullable()
    internal val structureId = reference("user_id", Structures.id).nullable()
    internal val firstName = varchar("first_name", 30)
    internal val lastName = varchar("last_name", 30)
    internal val patronymic = varchar("patronymic", 30).nullable()
    internal val accessToDirections = varchar("access_to_events", 256).nullable()
    override val primaryKey = PrimaryKey(id, name = "PK_Worker_Id")

    override suspend fun insertWorker(worker: WorkerDTO): Boolean = dbQuery {
        Workers.insert {
            it[personnelNumber] = worker.personnelNumber
            it[userId] = worker.userId
            it[structureId] = worker.structureId
            it[firstName] = worker.firstName
            it[lastName] = worker.lastName
            it[patronymic] = worker.patronymic
            it[accessToDirections] = worker.accessToDirections?.joinToString(",")
        }.insertedCount > 0
    }

    override suspend fun updateWorker(worker: WorkerDTO): Boolean = dbQuery {
        Workers.update({ id eq worker.id }) {
            it[personnelNumber] = worker.personnelNumber
            it[userId] = worker.userId
            it[structureId] = worker.structureId
            it[firstName] = worker.firstName
            it[lastName] = worker.lastName
            it[patronymic] = worker.patronymic
            it[accessToDirections] = worker.accessToDirections?.joinToString(",")
        } > 0
    }

    override suspend fun getWorker(id: Int): WorkerDTO? = dbQuery {
        Workers.select { Workers.id eq id }.singleOrNull()?.toWorkerDTO()
    }

    override suspend fun deleteWorker(id: Int): Boolean = dbQuery {
        Workers.deleteWhere { Workers.id eq id } > 0
    }
}