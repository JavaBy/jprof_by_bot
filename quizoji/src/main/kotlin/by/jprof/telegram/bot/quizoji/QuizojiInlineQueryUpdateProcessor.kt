package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.quizoji.dao.QuizojiDAO
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerInlineQuery
import dev.inmo.tgbotapi.extensions.utils.asInlineQueryUpdate
import dev.inmo.tgbotapi.types.InlineQueries.InlineQueryResult.InlineQueryResultArticle
import dev.inmo.tgbotapi.types.InlineQueries.InputMessageContent.InputTextMessageContent
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import org.apache.logging.log4j.LogManager

@PreviewFeature
class QuizojiInlineQueryUpdateProcessor(
    private val quizojiDAO: QuizojiDAO,
    private val votesDAO: VotesDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(QuizojiInlineQueryUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val inlineQuery = update.asInlineQueryUpdate()?.data ?: return

        if (inlineQuery.query.equals("quizoji", ignoreCase = true)) {
            bot.answerInlineQuery(
                inlineQuery = inlineQuery,
                switchPmText = "Create new quizoji",
                switchPmParameter = "quizoji",
            )
        } else if (inlineQuery.query.startsWith("quizoji")) {
            val id = inlineQuery.query.substring(7).trim()

            logger.debug("Quizoji #{} requested", id)

            val quizoji = quizojiDAO.get(id)

            if (null == quizoji) {
                logger.warn("Quizoji #{} not found!", id)

                return
            }

            val votes = votesDAO.get("QUIZOJI-$id")

            if (null == votes) {
                logger.warn("Votes for quizoji #{} not found!", id)

                return
            }

            when (val question = quizoji.question) {
                is TextContent -> {
                    bot.answerInlineQuery(
                        inlineQuery = inlineQuery,
                        results = listOf(
                            InlineQueryResultArticle(
                                id = quizoji.id,
                                title = "Quizoji with ${votes.options.joinToString()} options",
                                inputMessageContent = InputTextMessageContent(
                                    entities = question.textSources
                                ),
                                replyMarkup = votes.toInlineKeyboardMarkup(8)
                            )
                        )
                    )
                }

                else -> {}
            }
        }
    }
}
