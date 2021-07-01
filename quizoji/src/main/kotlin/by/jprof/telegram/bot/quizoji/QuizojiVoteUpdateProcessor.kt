package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import by.jprof.telegram.bot.votes.voting_processor.VotingProcessor
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update

class QuizojiVoteUpdateProcessor(
    votesDAO: VotesDAO,
    bot: RequestsExecutor
) : VotingProcessor(
    "QUIZOJI",
    votesDAO,
    { throw UnsupportedOperationException("Votes should be constructed elsewhere!") },
    bot,
), UpdateProcessor {
    override suspend fun process(update: Update) {
        when (update) {
            is CallbackQueryUpdate -> processCallbackQuery(update.data)
        }
    }

    override fun votesToInlineKeyboardMarkup(votes: Votes) = votes.toInlineKeyboardMarkup(8)
}
