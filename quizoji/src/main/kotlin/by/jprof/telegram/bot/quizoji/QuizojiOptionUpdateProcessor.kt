package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.dialogs.dao.DialogStateDAO
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForOptions
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateContentMessage
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class QuizojiOptionUpdateProcessor(
    private val dialogStateDAO: DialogStateDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(QuizojiOptionUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asPrivateContentMessage() ?: return
        val chat = message.chat.asPrivateChat() ?: return
        val state = dialogStateDAO.get(chat.id.chatId, message.user.id.chatId)
        val content = message.content

        if (state !is WaitingForOptions) {
            return
        }

        if (content !is TextContent) {
            logger.warn("Unsupported option content: {}", content)

            bot.sendMessage(
                chat = chat,
                text = "Unsupported option type: ${content::class.simpleName?.replace("Content", "")}"
            )

            return
        }

        if (content.textSources.any { it is BotCommandTextSource }) {
            logger.warn("Command sent as option: {}", content)

            return
        }

        logger.debug("{} provided an option ({}) for his Quizoji", chat.id.chatId, content.text)

        dialogStateDAO.save(
            WaitingForOptions(
                chatId = chat.id.chatId,
                userId = message.user.id.chatId,
                question = state.question,
                options = state.options + content.text
            )
        )

        bot.sendMessage(
            chat = chat,
            text = if (state.options.size >= 7) {
                "Send more options or /done when ready\\."
            } else {
                "Send more options or /done when ready\\.\n\n_Up to ${7 - state.options.size} more options are recommended\\._"
            },
            parseMode = MarkdownV2
        )
    }
}
