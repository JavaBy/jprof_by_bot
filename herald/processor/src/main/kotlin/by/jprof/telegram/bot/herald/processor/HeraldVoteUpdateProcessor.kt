package by.jprof.telegram.bot.herald.processor

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.voting_processor.VotingProcessor
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class HeraldVoteUpdateProcessor(
    votesDAO: VotesDAO,
    bot: RequestsExecutor,
) : VotingProcessor(
    "HERALD",
    votesDAO,
    { throw UnsupportedOperationException("Votes should be constructed elsewhere!") },
    bot,
), UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(HeraldVoteUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        when (update) {
            is CallbackQueryUpdate -> processCallbackQuery(update.data)
        }
    }
}
