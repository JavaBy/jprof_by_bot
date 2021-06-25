package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.core.UpdateProcessor
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerInlineQuery
import dev.inmo.tgbotapi.extensions.utils.asInlineQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class QuizojiInlineQueryUpdateProcessor(
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
        }
    }
}
