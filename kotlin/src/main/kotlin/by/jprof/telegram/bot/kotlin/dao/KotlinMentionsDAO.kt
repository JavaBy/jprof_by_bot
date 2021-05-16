package by.jprof.telegram.bot.kotlin.dao

import by.jprof.telegram.bot.kotlin.model.KotlinMentions

interface KotlinMentionsDAO {
    suspend fun save(kotlinMentions: KotlinMentions)

    suspend fun get(chat: Long): KotlinMentions?
}
