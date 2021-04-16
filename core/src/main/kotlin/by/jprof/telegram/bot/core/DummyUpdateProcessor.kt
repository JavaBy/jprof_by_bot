package by.jprof.telegram.bot.core

import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class DummyUpdateProcessor : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(DummyUpdateProcessor::class.java)
    }

    override suspend fun process(update: Update) {
        logger.debug("Doing nothing with {}", update)
    }
}
