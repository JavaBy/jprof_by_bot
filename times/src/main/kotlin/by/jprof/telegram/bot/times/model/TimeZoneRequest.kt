package by.jprof.telegram.bot.times.model

import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.abstracts.Message

sealed interface TimeZoneValue {
    object Unrecognized : TimeZoneValue

    object Empty : TimeZoneValue

    data class Id(
        val id: String
    ) : TimeZoneValue

    data class Offset(
        val offset: Int
    ) : TimeZoneValue
}

data class TimeZoneRequest(
    val user: User,
    val chat: Chat,
    val request: Message,
    val value: TimeZoneValue,
)
