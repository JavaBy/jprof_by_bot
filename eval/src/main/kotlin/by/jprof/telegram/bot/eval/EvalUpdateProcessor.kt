package by.jprof.telegram.bot.eval

import by.jprof.telegram.bot.core.UpdateProcessor
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asPreTextSource
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import org.apache.logging.log4j.LogManager

@PreviewFeature
class EvalUpdateProcessor : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(EvalUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val message = update.asBaseMessageUpdate() ?: return
        val content = message.data.asContentMessage() ?: return
        val text = content.content.asTextContent() ?: return
        val codes = text.textSources.mapNotNull { it.asPreTextSource() }

        codes.forEach {
            logger.debug(it)
        }
    }
}
