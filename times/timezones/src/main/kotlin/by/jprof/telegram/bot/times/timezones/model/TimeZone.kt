package by.jprof.telegram.bot.times.timezones.model

data class TimeZone(
    val user: Long,
    val username: String? = null,
    val chat: Long,
    val zoneId: String? = null,
    val offset: Int? = null,
)
