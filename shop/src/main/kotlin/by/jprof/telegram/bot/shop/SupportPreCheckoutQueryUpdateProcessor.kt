package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.shop.payload.Payload
import by.jprof.telegram.bot.shop.payload.SupportPayload
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.payments.answerPreCheckoutQueryOk
import dev.inmo.tgbotapi.extensions.utils.asPreCheckoutQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

@OptIn(PreviewFeature::class)
class SupportPreCheckoutQueryUpdateProcessor(
    private val bot: RequestsExecutor,
    private val json: Json,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(SupportPreCheckoutQueryUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val preCheckoutQuery = update.asPreCheckoutQueryUpdate()?.data ?: return
        val payload = try {
            json.decodeFromString<Payload>(preCheckoutQuery.invoicePayload) as SupportPayload
        } catch (_: Exception) {
            return
        }

        logger.info("{}", payload)

        bot.answerPreCheckoutQueryOk(preCheckoutQuery)
    }
}
