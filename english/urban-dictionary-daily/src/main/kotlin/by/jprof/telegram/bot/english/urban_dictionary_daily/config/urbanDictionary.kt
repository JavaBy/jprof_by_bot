package by.jprof.telegram.bot.english.urban_dictionary_daily.config

import by.jprof.telegram.bot.english.urban_dictionary.KtorUrbanDictionaryClient
import by.jprof.telegram.bot.english.urban_dictionary.UrbanDictionaryClient
import org.koin.dsl.module

val urbanDictionaryModule = module {
    single<UrbanDictionaryClient> {
        KtorUrbanDictionaryClient()
    }
}
