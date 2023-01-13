package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.monies.dao.MoniesDAO
import by.jprof.telegram.bot.monies.model.Money
import by.jprof.telegram.bot.monies.model.Monies
import by.jprof.telegram.bot.shop.payload.Payload
import by.jprof.telegram.bot.shop.payload.PinsPayload
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
class PinsSuccessfulPaymentUpdateProcessor(
    private val bot: RequestsExecutor,
    private val json: Json,
    private val moniesDAO: MoniesDAO,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(PinsSuccessfulPaymentUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asPossiblyPaymentMessage() ?: return
        val user = (message as? FromUser)?.user ?: return
        val payment = (message.paymentInfo as? SuccessfulPaymentEvent)?.payment ?: return
        val payload = try {
            json.decodeFromString<Payload>(payment.invoicePayload) as PinsPayload
        } catch (_: Exception) {
            return
        }

        val monies = moniesDAO.get(user.id.chatId, payload.chat) ?: Monies(user.id.chatId, payload.chat)
        val pins = monies.pins ?: 0

        moniesDAO.save(
            monies.copy(
                monies = monies.monies + (Money.PINS to pins)
            )
        )
        bot.reply(message, "Thank you for the purchase!")
    }
}
