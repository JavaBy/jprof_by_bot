package by.jprof.telegram.bot.runners.lambda.config

import org.koin.core.qualifier.named
import org.koin.dsl.module

const val TOKEN_TELEGRAM_BOT = "TOKEN_TELEGRAM_BOT"
const val TOKEN_YOUTUBE_API = "TOKEN_YOUTUBE_API"
const val TABLE_VOTES = "TABLE_VOTES"
const val TABLE_YOUTUBE_CHANNELS_WHITELIST = "TABLE_YOUTUBE_CHANNELS_WHITELIST"
const val TABLE_KOTLIN_MENTIONS = "TABLE_KOTLIN_MENTIONS"

val envModule = module {
    listOf(
        TOKEN_TELEGRAM_BOT,
        TOKEN_YOUTUBE_API,
        TABLE_VOTES,
        TABLE_YOUTUBE_CHANNELS_WHITELIST,
        TABLE_KOTLIN_MENTIONS,
    ).forEach { variable ->
        single(named(variable)) {
            System.getenv(variable)!!
        }
    }
}
