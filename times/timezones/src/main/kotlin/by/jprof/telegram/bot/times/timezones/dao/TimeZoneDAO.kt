package by.jprof.telegram.bot.times.timezones.dao

import by.jprof.telegram.bot.times.timezones.model.TimeZone

interface TimeZoneDAO {
    suspend fun save(timeZone: TimeZone)

    suspend fun get(user: Long, chat: Long): TimeZone?
}
