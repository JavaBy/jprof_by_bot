package by.jprof.telegram.bot.eval.evaluator.config

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
