package by.jprof.telegram.bot.english.urban_dictionary_daily.config

import dev.inmo.tgbotapi.extensions.api.telegramBot
import org.koin.core.qualifier.named
import org.koin.dsl.module

val telegramModule = module {
    single {
        telegramBot(get<String>(named(TOKEN_TELEGRAM_BOT)))
    }
}
