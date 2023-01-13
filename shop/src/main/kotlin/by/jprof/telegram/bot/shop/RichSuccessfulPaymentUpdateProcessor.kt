package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.shop.payload.Payload
import by.jprof.telegram.bot.shop.payload.RichPayload
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.members.setChatAdministratorCustomTitle
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.asChatEventMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.types.chat.PrivateChat
import dev.inmo.tgbotapi.types.message.payments.SuccessfulPaymentEvent
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

@OptIn(PreviewFeature::class)
class RichSuccessfulPaymentUpdateProcessor(
    private val bot: RequestsExecutor,
    private val json: Json,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(RichSuccessfulPaymentUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data ?: return
        val chat = message.chat as? PrivateChat ?: return
        val payment = (message.asChatEventMessage()?.chatEvent as? SuccessfulPaymentEvent)?.payment ?: return

        val payload = try {
            json.decodeFromString<Payload>(payment.invoicePayload) as RichPayload
        } catch (_: Exception) {
            return
        }

        bot.setChatAdministratorCustomTitle(
            chatId = payload.chat.toChatId(),
            userId = chat.id,
            customTitle = payload.status
        )
        bot.reply(message, "Thank you for the purchase!")
    }
}
