package by.jprof.telegram.bot.leetcode

import by.jprof.telegram.bot.core.UpdateProcessor
import dev.inmo.tgbotapi.types.MessageEntity.textsources.TextLinkTextSource
import dev.inmo.tgbotapi.types.MessageEntity.textsources.URLTextSource
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class LeetCodeUpdateProcessor : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(LeetCodeUpdateProcessor::class.java)!!
        private val slugExtractor: SlugExtractor = ::NaiveRegexSlugExtractor
    }

    override suspend fun process(update: Update) {
        @Suppress("NAME_SHADOWING") val update = update as? MessageUpdate ?: return

        val slugs = extractSlugs(update.data) ?: return

        logger.debug("LeetCode slugs: {}", slugs)
    }

    private fun extractSlugs(message: Message): List<String>? =
        (message as? ContentMessage<*>)?.let { contentMessage ->
            (contentMessage.content as? TextContent)?.let { content ->
                content
                    .textSources
                    .mapNotNull {
                        (it as? URLTextSource)?.source ?: (it as? TextLinkTextSource)?.url
                    }
                    .mapNotNull {
                        slugExtractor(it)
                    }
            }
        }
}
