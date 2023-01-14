package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.shop.payload.Payload
import by.jprof.telegram.bot.shop.payload.SupportPayload
import dev.inmo.tgbotapi.abstracts.FromUser
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asPossiblyPaymentMessage
import dev.inmo.tgbotapi.types.message.payments.SuccessfulPaymentEvent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

@OptIn(PreviewFeature::class)
class SupportSuccessfulPaymentUpdateProcessor(
    private val bot: RequestsExecutor,
    private val json: Json,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(SupportSuccessfulPaymentUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asPossiblyPaymentMessage() ?: return
        val user = (message as? FromUser)?.user ?: return
        val payment = (message.paymentInfo as? SuccessfulPaymentEvent)?.payment ?: return
        val payload = try {
            json.decodeFromString<Payload>(payment.invoicePayload) as SupportPayload
        } catch (_: Exception) {
            return
        }

        bot.reply(message, "Thank you for the donation!")
    }
}
