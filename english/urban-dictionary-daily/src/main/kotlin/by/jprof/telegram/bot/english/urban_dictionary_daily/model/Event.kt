package by.jprof.telegram.bot.english.urban_dictionary_daily.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    @SerialName("Records")
    val records: List<Record>
)
