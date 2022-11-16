package by.jprof.telegram.bot.dialogs.model.quizoji

import by.jprof.telegram.bot.dialogs.model.DialogState
import dev.inmo.tgbotapi.types.message.content.MessageContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("WaitingForOptions")
data class WaitingForOptions(
    override val chatId: Long,
    override val userId: Long,
    val question: MessageContent,
    val options: List<String> = emptyList(),
) : DialogState
