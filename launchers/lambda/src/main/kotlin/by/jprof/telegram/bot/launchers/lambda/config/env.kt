package by.jprof.telegram.bot.launchers.lambda.config

import org.koin.core.qualifier.named
import org.koin.dsl.module

const val TOKEN_TELEGRAM_BOT = "TOKEN_TELEGRAM_BOT"
const val TOKEN_YOUTUBE_API = "TOKEN_YOUTUBE_API"
const val TABLE_VOTES = "TABLE_VOTES"
const val TABLE_YOUTUBE_CHANNELS_WHITELIST = "TABLE_YOUTUBE_CHANNELS_WHITELIST"
const val TABLE_KOTLIN_MENTIONS = "TABLE_KOTLIN_MENTIONS"
const val TABLE_DIALOG_STATES = "TABLE_DIALOG_STATES"
const val TABLE_QUIZOJIS = "TABLE_QUIZOJIS"
const val TABLE_MONIES = "TABLE_MONIES"
const val TABLE_PINS = "TABLE_PINS"
const val TABLE_TIMEZONES = "TABLE_TIMEZONES"
const val STATE_MACHINE_UNPINS = "STATE_MACHINE_UNPINS"

val envModule = module {
    listOf(
        TOKEN_TELEGRAM_BOT,
        TOKEN_YOUTUBE_API,
        TABLE_VOTES,
        TABLE_YOUTUBE_CHANNELS_WHITELIST,
        TABLE_KOTLIN_MENTIONS,
        TABLE_DIALOG_STATES,
        TABLE_QUIZOJIS,
        TABLE_MONIES,
        TABLE_PINS,
        TABLE_TIMEZONES,
        STATE_MACHINE_UNPINS,
    ).forEach { variable ->
        single(named(variable)) {
            System.getenv(variable)!!
        }
    }
}
