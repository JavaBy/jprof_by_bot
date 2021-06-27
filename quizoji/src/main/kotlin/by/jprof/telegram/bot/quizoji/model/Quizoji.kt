package by.jprof.telegram.bot.quizoji.model

import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent

data class Quizoji(
    val id: String,
    val question: MessageContent,
    val options: List<String>,
)
