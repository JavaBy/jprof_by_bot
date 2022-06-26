package by.jprof.telegram.bot.leetcode

import by.jprof.telegram.bot.core.UpdateProcessor
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.types.MessageEntity.textsources.TextLinkTextSource
import dev.inmo.tgbotapi.types.MessageEntity.textsources.URLTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.apache.logging.log4j.LogManager

class LeetCodeUpdateProcessor(
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(LeetCodeUpdateProcessor::class.java)!!
        private val slugExtractor: SlugExtractor = ::NaiveRegexSlugExtractor
        private val leetCodeClient: LeetCodeClient = GraphQLLeetCodeClient()
    }

    override suspend fun process(update: Update) {
        @Suppress("NAME_SHADOWING") val update = update as? MessageUpdate ?: return

        val slugs = extractSlugs(update.data)?.takeUnless { it.isEmpty() }?.distinct() ?: return

        logger.debug("LeetCode slugs: {}", slugs)

        val questions = coroutineScope {
            slugs.map { slug -> async { leetCodeClient.questionData(slug) } }.awaitAll()
        }.filterNotNull()

        logger.debug("Questions: {}", questions)

        questions.forEach { question ->
            bot.reply(
                to = update.data,
                text = questionInfo(question),
                parseMode = MarkdownV2ParseMode,
            )
        }
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

    private fun questionInfo(question: Question): String {
        return """
        |${question.level()} *${question.title.escapeMarkdownV2Common()}* \(${question.categoryTitle}\) ${question.paidIndicator()}
        |
        |${question.markdownContent()}
        |
        |_Likes: ${question.likes} / Dislikes: ${question.dislikes}_
    """.trimMargin()
    }
}
