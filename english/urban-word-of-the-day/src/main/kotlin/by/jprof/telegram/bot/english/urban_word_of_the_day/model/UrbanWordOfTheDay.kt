package by.jprof.telegram.bot.english.urban_word_of_the_day.model

import java.time.LocalDate

data class UrbanWordOfTheDay(
    val date: LocalDate,
    val word: String,
    val definition: String,
    val example: String?,
    val permalink: String,
)
