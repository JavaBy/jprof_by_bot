package by.jprof.telegram.bot.runners.lambda.config

import by.jprof.telegram.bot.votes.dao.VotesDAO
import org.koin.core.qualifier.named
import org.koin.dsl.module
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import by.jprof.telegram.bot.votes.dynamodb.dao.VotesDAO as DynamoDBVotesDAO

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
}
