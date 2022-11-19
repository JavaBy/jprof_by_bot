package by.jprof.telegram.bot.pins.model

import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.abstracts.Message
import java.time.Duration
import kotlin.math.ceil

sealed interface PinDuration {
    object Unrecognized : PinDuration

    data class Recognized(
        val duration: Duration,
    ) : PinDuration
}

data class PinRequest(
    val message: Message?,
    val user: User,
    val chat: Chat,
    val request: Message,
    val duration: PinDuration,
) {
    // 1h == 1 PIN, rounded up
    val price: Int
        get() = when (duration) {
            is PinDuration.Recognized -> ceil(duration.duration.seconds / 3600.0).toInt()
            PinDuration.Unrecognized -> throw IllegalArgumentException("Cannot calculate price for unrecognized duration!")
        }
}
