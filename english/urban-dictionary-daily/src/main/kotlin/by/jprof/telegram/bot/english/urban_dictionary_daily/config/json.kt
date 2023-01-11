package by.jprof.telegram.bot.english.urban_dictionary_daily.config

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val jsonModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
}
