package by.jprof.telegram.bot.runners.lambda.config

import by.jprof.telegram.bot.core.UpdateProcessingPipeline
import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.eval.EvalUpdateProcessor
import by.jprof.telegram.bot.jep.JEPUpdateProcessor
import by.jprof.telegram.bot.jep.JsoupJEPSummary
import by.jprof.telegram.bot.kotlin.KotlinMentionsUpdateProcessor
import by.jprof.telegram.bot.pins.PinCommandUpdateProcessor
import by.jprof.telegram.bot.pins.PinReplyUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiDoneCommandUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiInlineQueryUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiOptionUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiQuestionUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiStartCommandUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiVoteUpdateProcessor
import by.jprof.telegram.bot.youtube.YouTubeUpdateProcessor
import dev.inmo.tgbotapi.utils.PreviewFeature
import org.koin.core.qualifier.named
import org.koin.dsl.module

@PreviewFeature
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

    single<UpdateProcessor>(named("KotlinMentionsUpdateProcessor")) {
        KotlinMentionsUpdateProcessor(
            kotlinMentionsDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("QuizojiInlineQueryUpdateProcessor")) {
        QuizojiInlineQueryUpdateProcessor(
            quizojiDAO = get(),
            votesDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("QuizojiStartCommandUpdateProcessor")) {
        QuizojiStartCommandUpdateProcessor(
            dialogStateDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("QuizojiQuestionUpdateProcessor")) {
        QuizojiQuestionUpdateProcessor(
            dialogStateDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("QuizojiOptionUpdateProcessor")) {
        QuizojiOptionUpdateProcessor(
            dialogStateDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("QuizojiDoneCommandUpdateProcessor")) {
        QuizojiDoneCommandUpdateProcessor(
            dialogStateDAO = get(),
            quizojiDAO = get(),
            votesDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("QuizojiVoteUpdateProcessor")) {
        QuizojiVoteUpdateProcessor(
            votesDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("EvalUpdateProcessor")) {
        EvalUpdateProcessor()
    }

    single<UpdateProcessor>(named("PinCommandUpdateProcessor")) {
        PinCommandUpdateProcessor(
            moniesDAO = get(),
            pinDAO = get(),
            unpinScheduler = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("PinReplyUpdateProcessor")) {
        PinReplyUpdateProcessor(
            moniesDAO = get(),
            pinDAO = get(),
        )
    }
}
