package by.jprof.telegram.bot.jep

interface JEPSummary {
    suspend fun of(jep: String): String?
}
