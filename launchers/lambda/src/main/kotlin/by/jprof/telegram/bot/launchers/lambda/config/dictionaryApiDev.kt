package by.jprof.telegram.bot.launchers.lambda.config

import by.jprof.telegram.bot.english.dictionaryapi_dev.DictionaryAPIDotDevClient
import by.jprof.telegram.bot.english.dictionaryapi_dev.KtorDictionaryAPIDotDevClient
import org.koin.dsl.module

val dictionaryApiDevModule = module {
    single<DictionaryAPIDotDevClient> {
        KtorDictionaryAPIDotDevClient()
    }
}
