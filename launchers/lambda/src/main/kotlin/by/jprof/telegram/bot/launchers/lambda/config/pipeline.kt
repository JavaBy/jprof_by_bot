package by.jprof.telegram.bot.launchers.lambda.config

import by.jprof.telegram.bot.core.UpdateProcessingPipeline
import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.currencies.CurrenciesUpdateProcessor
import by.jprof.telegram.bot.english.EnglishCommandUpdateProcessor
import by.jprof.telegram.bot.english.ExplainerUpdateProcessor
import by.jprof.telegram.bot.english.UrbanWordOfTheDayUpdateProcessor
import by.jprof.telegram.bot.english.WhatWordUpdateProcessor
import by.jprof.telegram.bot.eval.EvalUpdateProcessor
import by.jprof.telegram.bot.jep.JEPUpdateProcessor
import by.jprof.telegram.bot.jep.JsoupJEPSummary
import by.jprof.telegram.bot.kotlin.KotlinMentionsUpdateProcessor
import by.jprof.telegram.bot.leetcode.LeetCodeUpdateProcessor
import by.jprof.telegram.bot.pins.PinCommandUpdateProcessor
import by.jprof.telegram.bot.pins.PinReplyUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiDoneCommandUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiInlineQueryUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiOptionUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiQuestionUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiStartCommandUpdateProcessor
import by.jprof.telegram.bot.quizoji.QuizojiVoteUpdateProcessor
import by.jprof.telegram.bot.times.TimeCommandUpdateProcessor
import by.jprof.telegram.bot.times.TimeZoneCommandUpdateProcessor
import by.jprof.telegram.bot.youtube.YouTubeUpdateProcessor
import dev.inmo.tgbotapi.utils.PreviewFeature
import org.koin.core.qualifier.named
import org.koin.dsl.module

@PreviewFeature
val pipelineModule = module {
    single {
        UpdateProcessingPipeline(
            processors = getAll(),
            timeout = get<Long>(named(TIMEOUT)) - 1000
        )
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

    single<UpdateProcessor>(named("CurrenciesUpdateProcessor")) {
        CurrenciesUpdateProcessor(
            monetaryAmountParsingPipeline = get(),
            exchangeRateClient = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("LeetCodeUpdateProcessor")) {
        LeetCodeUpdateProcessor(
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("TimeZoneCommandUpdateProcessor")) {
        TimeZoneCommandUpdateProcessor(
            timeZoneDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("TimeCommandUpdateProcessor")) {
        TimeCommandUpdateProcessor(
            timeZoneDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("EnglishCommandUpdateProcessor")) {
        EnglishCommandUpdateProcessor(
            languageRoomDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("UrbanWordOfTheDayUpdateProcessor")) {
        UrbanWordOfTheDayUpdateProcessor(
            languageRoomDAO = get(),
            urbanWordOfTheDayDAO = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("ExplainerUpdateProcessor")) {
        ExplainerUpdateProcessor(
            languageRoomDAO = get(),
            urbanDictionaryClient = get(),
            dictionaryapiDevClient = get(),
            bot = get(),
        )
    }

    single<UpdateProcessor>(named("WhatWordUpdateProcessor")) {
        WhatWordUpdateProcessor(
            languageRoomDAO = get(),
            bot = get(),
        )
    }
}
