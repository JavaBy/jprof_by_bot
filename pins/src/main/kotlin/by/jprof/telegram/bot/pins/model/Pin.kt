package by.jprof.telegram.bot.pins.model

data class Pin(
    val chatId: Long,
    val messageId: Long,
    val userId: Long,
)
