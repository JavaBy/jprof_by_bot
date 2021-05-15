package by.jprof.telegram.bot.youtube.dao

import by.jprof.telegram.bot.youtube.model.YouTubeChannel

interface YouTubeChannelsWhitelistDAO {
    suspend fun get(id: String): YouTubeChannel?
}
