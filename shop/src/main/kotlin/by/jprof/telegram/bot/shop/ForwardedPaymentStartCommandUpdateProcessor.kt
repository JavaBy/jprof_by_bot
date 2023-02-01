package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.shop.utils.forwardedPaymentsAreNotSupported
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature

@PreviewFeature
class ForwardedPaymentStartCommandUpdateProcessor(
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asPrivateContentMessage() ?: return
        val chat = message.chat.asPrivateChat() ?: return
        val content = message.content.asTextContent() ?: return

        if (content.text != "/start forwarded_payment") {
            return
        }

        bot.sendMessage(
            chat = chat,
            text = forwardedPaymentsAreNotSupported(),
            parseMode = MarkdownV2ParseMode,
        )
    }
}
