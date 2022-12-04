package by.jprof.telegram.bot.english.urban_word_of_the_day.dao

import by.jprof.telegram.bot.english.urban_word_of_the_day.model.UrbanWordOfTheDay
import java.time.LocalDate

interface UrbanWordOfTheDayDAO {
    suspend fun save(urbanWordOfTheDay: UrbanWordOfTheDay)

    suspend fun get(date: LocalDate): UrbanWordOfTheDay?
}
