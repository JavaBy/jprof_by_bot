package by.jprof.telegram.bot.english.urban_dictionary_daily.model

import kotlinx.serialization.Serializable

@Serializable
data class Mail(
    val headers: List<Header>
) {
    val subject: String?
        get() = headers.firstOrNull { it.name == "Subject" }?.value
}
