package by.jprof.telegram.bot.currencies

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.currencies.parser.MonetaryAmountParsingPipeline
import by.jprof.telegram.bot.currencies.rates.ExchangeRateClient
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import org.apache.logging.log4j.LogManager

class CurrenciesUpdateProcessor(
    private val monetaryAmountParsingPipeline: MonetaryAmountParsingPipeline,
    private val exchangeRateClient: ExchangeRateClient,
    private val targetCurrencies: List<String> = listOf("EUR", "USD"),
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(CurrenciesUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        @Suppress("NAME_SHADOWING") val update = update as? MessageUpdate ?: return
        val message = update.data as? ContentMessage<*> ?: return
        val content = message.content as? TextContent ?: return

        val monetaryAmounts = monetaryAmountParsingPipeline.parse(content.text)

        logger.debug("Parsed monetary amounts: {}", monetaryAmounts)

        val conversions = targetCurrencies.flatMap { to ->
            monetaryAmounts.mapNotNull {
                exchangeRateClient.convert(it.amount, it.currency, to)
            }
        }.groupBy { it.query.amount to it.query.from }

        val reply = conversions
            .map { (query, conversions) ->
                val (amount, from) = query
                val results = conversions
                    .sortedBy { it.query.to }
                    .joinToString(", ") { "%.2f %s".format(it.result, it.query.to).escapeMarkdownV2Common() }

                "**${"%.2f %s".format(amount, from).escapeMarkdownV2Common()}**: %s".format(results)
            }
            .joinToString("\n")

        if (reply.isNotEmpty()) {
            bot.reply(to = message, text = reply, parseMode = MarkdownV2ParseMode)
        }
    }
}
