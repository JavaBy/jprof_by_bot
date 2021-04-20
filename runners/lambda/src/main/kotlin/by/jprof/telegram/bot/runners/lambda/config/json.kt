package by.jprof.telegram.bot.runners.lambda.config

import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val jsonModule = module {
    single {
        Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }
    }
}
