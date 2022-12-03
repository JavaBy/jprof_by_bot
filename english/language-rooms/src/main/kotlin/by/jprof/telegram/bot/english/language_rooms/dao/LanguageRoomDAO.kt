package by.jprof.telegram.bot.english.language_rooms.dao

import by.jprof.telegram.bot.english.language_rooms.model.LanguageRoom

interface LanguageRoomDAO {
    suspend fun save(languageRoom: LanguageRoom)

    suspend fun get(chatId: Long, threadId: Long? = null): LanguageRoom?

    suspend fun getAll(): List<LanguageRoom>

    suspend fun delete(chatId: Long, threadId: Long? = null)
}
