package by.jprof.telegram.bot.jep

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import dev.inmo.tgbotapi.CommonAbstracts.justTextSources
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.ReplyMarkup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.types.CallbackQuery.CallbackQuery
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.MessageEntity.textsources.TextLinkTextSource
import dev.inmo.tgbotapi.types.MessageEntity.textsources.URLTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.apache.logging.log4j.LogManager

class JEPUpdateProcessor(
    private val jepSummary: JEPSummary,
    private val votesDAO: VotesDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        val logger = LogManager.getLogger(JEPUpdateProcessor::class.java)!!
        val linkRegex = "https?://openjdk\\.java\\.net/jeps/(\\d+)/?".toRegex()
    }

    override suspend fun process(update: Update) {
        when (update) {
            is MessageUpdate -> processMessage(update.data)
            is CallbackQueryUpdate -> processCallbackQuery(update.data)
        }
    }

    private suspend fun processMessage(message: Message) {
        logger.debug("Processing message: {}", message)

        val jeps = extractJEPs(message) ?: return

        logger.debug("JEP mentions: {}", jeps)

        supervisorScope {
            jeps
                .map { launch { replyToJEPMention(it, message) } }
                .joinAll()
        }
    }

    private suspend fun processCallbackQuery(callbackQuery: CallbackQuery) {
        logger.debug("Processing callback query: {}", callbackQuery)

        (callbackQuery as? MessageDataCallbackQuery)?.data?.takeIf { it.startsWith("JEP") }?.let { data ->
            val (votesId, vote) = data.split(":").takeIf { it.size == 2 } ?: return
            val fromUserId = callbackQuery.user.id.chatId.toString()

            logger.debug("Tracking {}'s '{}' vote for {}", fromUserId, vote, votesId)

            val votes = votesDAO.get(votesId) ?: votesId.toVotes()
            val updatedVotes = votes.copy(votes = votes.votes + (fromUserId to vote))

            votesDAO.save(updatedVotes)
            bot.answerCallbackQuery(callbackQuery)
            bot.editMessageReplyMarkup(
                message = callbackQuery.message,
                replyMarkup = updatedVotes.toInlineKeyboardMarkup()
            )
        }
    }

    private fun extractJEPs(message: Message): List<String>? =
        (message as? ContentMessage<*>)?.let { contentMessage ->
            (contentMessage.content as? TextContent)?.let { content ->
                content
                    .textEntities
                    .justTextSources()
                    .mapNotNull {
                        (it as? URLTextSource)?.source ?: (it as? TextLinkTextSource)?.url
                    }
                    .mapNotNull {
                        linkRegex.matchEntire(it)?.destructured
                    }
                    .map { (jep) -> jep }
            }
        }

    private suspend fun replyToJEPMention(jep: String, message: Message) {
        logger.debug("Replying to JEP-{}", jep)

        val summary = jepSummary.of(jep)

        logger.debug("Summary for JEP {}: {}", jep, summary)

        val text = if (summary != null) {
            "${summary.escapeMarkdownV2Common()}\n\nCast your vote for *JEP $jep* now ⤵️"
        } else {
            "Cast your vote for *JEP $jep* now ⤵️"
        }
        val votesId = jep.toVotesID()
        val votes = votesDAO.get(votesId) ?: votesId.toVotes()

        bot.reply(
            to = message,
            text = text,
            parseMode = MarkdownV2ParseMode,
            replyMarkup = votes.toInlineKeyboardMarkup()
        )
    }

    private fun String.toVotesID() = "JEP-$this"

    private fun String.toVotes() = Votes(this, listOf("\uD83D\uDC4D", "\uD83D\uDC4E"))
}
