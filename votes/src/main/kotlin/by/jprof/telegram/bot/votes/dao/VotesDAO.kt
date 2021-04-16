package by.jprof.telegram.bot.votes.dao

import by.jprof.telegram.bot.votes.model.Votes

interface VotesDAO {
    suspend fun save(votes: Votes)

    suspend fun get(id: String): Votes?
}
