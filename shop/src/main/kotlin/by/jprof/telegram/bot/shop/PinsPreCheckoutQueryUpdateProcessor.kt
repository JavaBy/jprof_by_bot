package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.monies.dao.MoniesDAO
import by.jprof.telegram.bot.monies.model.Monies
import by.jprof.telegram.bot.shop.payload.PinsPayload
import by.jprof.telegram.bot.shop.utils.tooManyPins
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.payments.answerPreCheckoutQueryError
import dev.inmo.tgbotapi.extensions.api.answers.payments.answerPreCheckoutQueryOk
import dev.inmo.tgbotapi.extensions.utils.asPreCheckoutQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

@OptIn(PreviewFeature::class)
class PinsPreCheckoutQueryUpdateProcessor(
    private val bot: RequestsExecutor,
    private val json: Json,
    private val moniesDAO: MoniesDAO,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(PinsPreCheckoutQueryUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val preCheckoutQuery = update.asPreCheckoutQueryUpdate()?.data ?: return

        logger.debug(preCheckoutQuery)

        val payload = try {
            json.decodeFromString<PinsPayload>(preCheckoutQuery.invoicePayload)
        } catch (_: Exception) {
            return
        }

        logger.debug(payload)

        val monies = moniesDAO.get(preCheckoutQuery.user.id.chatId, payload.chat) ?: Monies(preCheckoutQuery.user.id.chatId, payload.chat)
        val pins = monies.pins ?: 0

        if (pins > 9999) {
            bot.answerPreCheckoutQueryError(preCheckoutQuery, tooManyPins())
        } else {
            bot.answerPreCheckoutQueryOk(preCheckoutQuery)
        }
    }
}
