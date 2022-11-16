package by.jprof.telegram.bot.quizoji.model

import  dev.inmo.tgbotapi.types.message.content.MessageContent

data class Quizoji(
    val id: String,
    val question: MessageContent,
)
