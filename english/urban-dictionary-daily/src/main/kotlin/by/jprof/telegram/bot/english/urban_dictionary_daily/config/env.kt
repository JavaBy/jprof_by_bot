package by.jprof.telegram.bot.english.urban_dictionary_daily.config

import org.koin.core.qualifier.named
import org.koin.dsl.module

const val TOKEN_TELEGRAM_BOT = "TOKEN_TELEGRAM_BOT"
const val TABLE_URBAN_WORDS_OF_THE_DAY = "TABLE_URBAN_WORDS_OF_THE_DAY"
const val TABLE_LANGUAGE_ROOMS = "TABLE_LANGUAGE_ROOMS"
const val STATE_MACHINE_UNPINS = "STATE_MACHINE_UNPINS"

val envModule = module {
    listOf(
        TOKEN_TELEGRAM_BOT,
        TABLE_URBAN_WORDS_OF_THE_DAY,
        TABLE_LANGUAGE_ROOMS,
        STATE_MACHINE_UNPINS,
    ).forEach { variable ->
        single(named(variable)) {
            System.getenv(variable)!!
        }
    }
}
