package by.jprof.telegram.bot.kotlin.model

import java.time.Instant

data class KotlinMentions(
    val chat: Long,
    val lastMention: Instant,
    val usersStatistics: Map<Long, UserStatistics> = emptyMap(),
)

data class UserStatistics(
    val count: Long,
    val lastMention: Instant,
)
