package by.jprof.telegram.bot.runners.lambda.config

import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.youtube.dao.YouTubeChannelsWhitelistDAO
import org.koin.core.qualifier.named
import org.koin.dsl.module
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
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
}
