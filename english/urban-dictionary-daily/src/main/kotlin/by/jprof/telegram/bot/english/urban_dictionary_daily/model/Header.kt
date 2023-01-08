package by.jprof.telegram.bot.english.urban_dictionary_daily.model

import kotlinx.serialization.Serializable

@Serializable
data class Header(
    val name: String,
    val value: String,
)
