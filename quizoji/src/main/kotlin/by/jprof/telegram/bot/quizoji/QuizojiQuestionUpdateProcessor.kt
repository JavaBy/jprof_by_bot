package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.dialogs.dao.DialogStateDAO
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForOptions
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForQuestion
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateContentMessage
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import org.apache.logging.log4j.LogManager
import kotlin.reflect.KClass

@PreviewFeature
class QuizojiQuestionUpdateProcessor(
    private val dialogStateDAO: DialogStateDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(QuizojiQuestionUpdateProcessor::class.java)!!
        private val supportedTypes = setOf<KClass<out MessageContent>>(
            TextContent::class
        )
    }

    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asPrivateContentMessage() ?: return
        val chat = message.chat.asPrivateChat() ?: return
        val state = dialogStateDAO.get(chat.id.chatId, message.user.id.chatId)

        if (state !is WaitingForQuestion) {
            return
        }

        val content = message.content

        if (content::class !in supportedTypes) {
            logger.warn("Unsupported message content: {}", content)

            bot.sendMessage(
                chat = chat,
                text = "Unsupported question type: ${content::class.simpleName?.replace("Content", "")}"
            )

            return
        }

        if ((content as? TextContent)?.textSources?.any { it is BotCommandTextSource } == true) {
            logger.warn("Command sent as question: {}", content)

            return
        }

        logger.debug("{} provided a question ({}) for his Quizoji", chat.id.chatId, content)

        dialogStateDAO.save(
            WaitingForOptions(
                chatId = chat.id.chatId,
                userId = message.user.id.chatId,
                question = content
            )
        )

        bot.sendMessage(
            chat = chat,
            text = "Now send me the options, one per message\\. When done, send /done\\.\n\n_Up to 8 options are recommended, otherwise the buttons will be wrapped in multiple lines\\._",
            parseMode = MarkdownV2
        )
    }
}
