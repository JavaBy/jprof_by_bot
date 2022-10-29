package by.jprof.telegram.bot.launchers.lambda.config

import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import org.koin.core.qualifier.named
import org.koin.dsl.module

val telegramModule = module {
    single {
        telegramBot(get<String>(named(TOKEN_TELEGRAM_BOT)))
    }
}
