package by.jprof.telegram.bot.dialogs.dao

import by.jprof.telegram.bot.dialogs.model.DialogState

interface DialogStateDAO {
    suspend fun get(chatId: Long, userId: Long): DialogState?

    suspend fun save(entity: DialogState)

    suspend fun delete(chatId: Long, userId: Long)
}
