package by.jprof.telegram.bot.runners.lambda.config

import by.jprof.telegram.bot.core.DummyUpdateProcessor
import by.jprof.telegram.bot.core.UpdateProcessingPipeline
import by.jprof.telegram.bot.core.UpdateProcessor
import org.koin.core.qualifier.named
import org.koin.dsl.module

val pipelineModule = module {
    single {
        UpdateProcessingPipeline(getAll())
    }

    single<UpdateProcessor>(named("DummyUpdateProcessor")) {
        DummyUpdateProcessor()
    }
}
