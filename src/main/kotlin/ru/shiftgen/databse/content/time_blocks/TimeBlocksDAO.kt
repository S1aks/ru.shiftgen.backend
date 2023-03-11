package ru.shiftgen.databse.content.time_blocks

import org.jetbrains.exposed.sql.ResultRow
import ru.shiftgen.databse.content.enums.Action

interface TimeBlocksDAO {
    fun ResultRow.toTimeBlockDTO() = TimeBlockDTO(
        id = this[TimeBlocks.id],
        name = this[TimeBlocks.name],
        duration = this[TimeBlocks.duration],
        action = Action.values()[this[TimeBlocks.action]]
    )

    suspend fun insertTimeBlock(timeBlock: TimeBlockDTO): Boolean
    suspend fun updateTimeBlock(timeBlock: TimeBlockDTO): Boolean
    suspend fun getTimeBlock(id: Int): TimeBlockDTO?
    suspend fun deleteTimeBlock(id: Int): Boolean
}