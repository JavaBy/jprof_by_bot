package by.jprof.telegram.bot.english.urban_dictionary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Definition(
    val definition: String,
    val permalink: String,
    val author: String,
    val word: String,
    val example: String,
    @SerialName("thumbs_up")
    val thumbsUp: Int,
    @SerialName("thumbs_down")
    val thumbsDown: Int,
)
