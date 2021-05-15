package by.jprof.telegram.bot.runners.lambda.config

import by.jprof.telegram.bot.core.UpdateProcessingPipeline
import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.jep.JEPUpdateProcessor
import by.jprof.telegram.bot.jep.JsoupJEPSummary
import by.jprof.telegram.bot.youtube.YouTubeUpdateProcessor
import org.koin.core.qualifier.named
import org.koin.dsl.module

val pipelineModule = module {
    single {
        UpdateProcessingPipeline(getAll())
    }

    single<UpdateProcessor>(named("JEPUpdateProcessor")) {
        JEPUpdateProcessor(
            bot = get(),
            jepSummary = JsoupJEPSummary(),
            votesDAO = get(),
        )
    }

    single<UpdateProcessor>(named("YouTubeUpdateProcessor")) {
        YouTubeUpdateProcessor(
            bot = get(),
            votesDAO = get(),
            youTubeChannelsWhitelistDAO = get(),
            youTube = get(),
        )
    }
}
