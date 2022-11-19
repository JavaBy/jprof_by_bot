package by.jprof.telegram.bot.launchers.lambda.config

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val jsonModule = module {
    single {
        Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }
    }
}
