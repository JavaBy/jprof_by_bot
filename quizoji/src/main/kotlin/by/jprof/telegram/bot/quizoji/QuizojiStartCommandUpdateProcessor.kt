package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.core.UpdateProcessor
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class QuizojiStartCommandUpdateProcessor(
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(QuizojiStartCommandUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asPrivateContentMessage() ?: return
        val chat = message.chat.asPrivateChat() ?: return
        val content = message.content.asTextContent() ?: return

        if (content.text != "/start quizoji") {
            return
        }

        bot.sendMessage(
            chat = chat,
            text = "Let's create a Quizoji! First, send me the message. It can be anything — a text, photo, video, even a sticker."
        )
    }
}
