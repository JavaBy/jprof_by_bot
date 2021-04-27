package by.jprof.telegram.bot.votes.voting_processor

import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.ReplyMarkup.editMessageReplyMarkup
import dev.inmo.tgbotapi.types.CallbackQuery.CallbackQuery
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import org.apache.logging.log4j.LogManager

abstract class VotingProcessor(
    private val prefix: String,
    private val votesDAO: VotesDAO,
    private val votesConstructor: (String) -> Votes,
    private val bot: RequestsExecutor,
) {
    companion object {
        val logger = LogManager.getLogger(VotingProcessor::class.java)!!
    }

    suspend fun processCallbackQuery(callbackQuery: CallbackQuery) {
        logger.debug("Processing callback query: {}", callbackQuery)

        (callbackQuery as? MessageDataCallbackQuery)?.data?.takeIf { it.startsWith(prefix) }?.let { data ->
            val (votesId, vote) = data.split(":").takeIf { it.size == 2 } ?: return
            val fromUserId = callbackQuery.user.id.chatId.toString()

            logger.debug("Tracking {}'s '{}' vote for {}", fromUserId, vote, votesId)

            val votes = votesDAO.get(votesId) ?: votesConstructor(votesId)
            val updatedVotes = votes.copy(votes = votes.votes + (fromUserId to vote))

            votesDAO.save(updatedVotes)
            bot.answerCallbackQuery(callbackQuery)
            bot.editMessageReplyMarkup(
                message = callbackQuery.message,
                replyMarkup = updatedVotes.toInlineKeyboardMarkup()
            )
        }
    }
}
