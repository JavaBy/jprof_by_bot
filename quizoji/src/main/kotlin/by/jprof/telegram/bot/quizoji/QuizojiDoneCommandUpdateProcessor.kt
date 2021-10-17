package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.dialogs.dao.DialogStateDAO
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForOptions
import by.jprof.telegram.bot.quizoji.dao.QuizojiDAO
import by.jprof.telegram.bot.quizoji.model.Quizoji
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import org.apache.logging.log4j.LogManager

@PreviewFeature
class QuizojiDoneCommandUpdateProcessor(
    private val dialogStateDAO: DialogStateDAO,
    private val quizojiDAO: QuizojiDAO,
    private val votesDAO: VotesDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(QuizojiDoneCommandUpdateProcessor::class.java)!!
        private val idChars = ('0'..'9') + ('a'..'z') + ('A'..'Z')
        private const val idLength = 20
    }

    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asPrivateContentMessage() ?: return
        val chat = message.chat.asPrivateChat() ?: return
        val state = dialogStateDAO.get(chat.id.chatId, message.user.id.chatId)

        if (state !is WaitingForOptions) {
            return
        }

        val content = message.content.asTextContent() ?: return

        if (content.text != "/done") {
            return
        }

        if (state.options.isEmpty()) {
            bot.sendMessage(
                chat = chat,
                text = "Please, provide some options for your quizoji!"
            )

            return
        }

        logger.debug("{} finished his Quizoji", chat.id.chatId)

        val quizoji = Quizoji(
            id = generateQuizojiID(),
            question = state.question,
        )
        val votes = Votes(
            id = "QUIZOJI-${quizoji.id}",
            options = state.options
        )

        votesDAO.save(votes)
        quizojiDAO.save(quizoji)
        dialogStateDAO.delete(chatId = chat.id.chatId, userId = message.user.id.chatId)

        bot.sendMessage(
            chat = chat,
            text = "Quizoji created! Use the 'Publish' button to send it."
        )

        when (val question = state.question) {
            is TextContent -> {
                bot.sendMessage(
                    chat = chat,
                    entities = question.textSources,
                    replyMarkup = InlineKeyboardMarkup(
                        votes
                            .toInlineKeyboardMarkup(8)
                            .keyboard
                            .plus(
                                listOf(
                                    listOf(
                                        SwitchInlineQueryInlineKeyboardButton(
                                            text = "Publish",
                                            switchInlineQuery = "quizoji ${quizoji.id}",
                                        )
                                    )
                                )
                            )
                    )
                )
            }
        }
    }

    private fun generateQuizojiID(): String {
        return (0 until idLength).map { idChars.random() }.joinToString(separator = "")
    }
}
