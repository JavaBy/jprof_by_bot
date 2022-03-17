package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount
import org.apache.logging.log4j.LogManager

class MonetaryAmountParsingPipeline(
    private val parsers: List<MonetaryAmountParser>
) {
    companion object {
        private val logger = LogManager.getLogger(MonetaryAmountParsingPipeline::class.java)!!
    }

    fun parse(message: String): List<MonetaryAmount> = parsers.flatMap {
        try {
            it(message)
        } catch (e: Exception) {
            logger.error("Exception in ${it::class.simpleName}", e)
            emptyList()
        }
    }
}
