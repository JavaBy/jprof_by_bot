package by.jprof.telegram.bot.pins.unpin.config

import org.koin.core.qualifier.named
import org.koin.dsl.module

const val TOKEN_TELEGRAM_BOT = "TOKEN_TELEGRAM_BOT"
const val TABLE_PINS = "TABLE_PINS"

val envModule = module {
    listOf(
        TOKEN_TELEGRAM_BOT,
        TABLE_PINS,
    ).forEach { variable ->
        single(named(variable)) {
            System.getenv(variable)!!
        }
    }
}
