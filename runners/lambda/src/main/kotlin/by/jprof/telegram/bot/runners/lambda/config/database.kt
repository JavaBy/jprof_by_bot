package by.jprof.telegram.bot.runners.lambda.config

import by.jprof.telegram.bot.dialogs.dao.DialogStateDAO
import by.jprof.telegram.bot.kotlin.dao.KotlinMentionsDAO
import by.jprof.telegram.bot.quizoji.dao.QuizojiDAO
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.youtube.dao.YouTubeChannelsWhitelistDAO
import org.koin.core.qualifier.named
import org.koin.dsl.module
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import by.jprof.telegram.bot.dialogs.dynamodb.dao.DialogStateDAO as DynamoDBDialogStateDAO
import by.jprof.telegram.bot.kotlin.dynamodb.dao.KotlinMentionsDAO as DynamoDBKotlinMentionsDAO
import by.jprof.telegram.bot.quizoji.dynamodb.dao.QuizojiDAO as DynamoDBQuizojiDAO
import by.jprof.telegram.bot.votes.dynamodb.dao.VotesDAO as DynamoDBVotesDAO
import by.jprof.telegram.bot.youtube.dynamodb.dao.YouTubeChannelsWhitelistDAO as DynamoDBYouTubeChannelsWhitelistDAO

val databaseModule = module {
    single {
        DynamoDbAsyncClient.create()
    }

    single<VotesDAO> {
        DynamoDBVotesDAO(
            get(),
            get(named(TABLE_VOTES))
        )
    }

    single<YouTubeChannelsWhitelistDAO> {
        DynamoDBYouTubeChannelsWhitelistDAO(
            get(),
            get(named(TABLE_YOUTUBE_CHANNELS_WHITELIST))
        )
    }

    single<KotlinMentionsDAO> {
        DynamoDBKotlinMentionsDAO(
            get(),
            get(named(TABLE_KOTLIN_MENTIONS))
        )
    }

    single<DialogStateDAO> {
        DynamoDBDialogStateDAO(
            get(),
            get(named(TABLE_DIALOG_STATES))
        )
    }

    single<QuizojiDAO> {
        DynamoDBQuizojiDAO(
            get(),
            get(named(TABLE_QUIZOJIS))
        )
    }
}
