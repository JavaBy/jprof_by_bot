package by.jprof.telegram.bot.runners.lambda.config

import org.koin.core.qualifier.named
import org.koin.dsl.module

val envModule = module {
    emptyList<String>().forEach { variable ->
        single(named(variable)) {
            System.getenv(variable)!!
        }
    }
}
