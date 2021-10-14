package by.jprof.telegram.bot.monies.dao

import by.jprof.telegram.bot.monies.model.Monies

interface MoniesDAO {
    suspend fun save(monies: Monies)

    suspend fun get(user: Long, chat: Long): Monies?
}
