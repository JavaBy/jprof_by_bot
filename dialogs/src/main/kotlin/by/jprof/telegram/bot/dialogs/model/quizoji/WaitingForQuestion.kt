package by.jprof.telegram.bot.dialogs.model.quizoji

import by.jprof.telegram.bot.dialogs.model.DialogState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("WaitingForQuestion")
data class WaitingForQuestion(
    override val chatId: Long,
    override val userId: Long
) : DialogState
