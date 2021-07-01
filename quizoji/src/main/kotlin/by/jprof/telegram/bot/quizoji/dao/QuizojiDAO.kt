package by.jprof.telegram.bot.quizoji.dao

import by.jprof.telegram.bot.quizoji.model.Quizoji

interface QuizojiDAO {
    suspend fun save(quizoji: Quizoji)

    suspend fun get(id: String): Quizoji?
}
