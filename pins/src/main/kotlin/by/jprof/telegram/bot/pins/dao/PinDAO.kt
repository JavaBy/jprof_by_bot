package by.jprof.telegram.bot.pins.dao

import by.jprof.telegram.bot.pins.model.Pin

interface PinDAO {
    suspend fun save(pin: Pin)

    suspend fun get(chatId: Long, messageId: Long): Pin?

    suspend fun findByChatId(chatId: Long): List<Pin>

    suspend fun findByUserId(userId: Long): List<Pin>

    suspend fun delete(chatId: Long, messageId: Long)
}
