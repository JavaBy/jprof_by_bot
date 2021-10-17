package by.jprof.telegram.bot.pins.unpin.config

import by.jprof.telegram.bot.pins.dao.PinDAO
import org.koin.core.qualifier.named
import org.koin.dsl.module
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import by.jprof.telegram.bot.pins.dynamodb.dao.PinDAO as DynamoDBPinDAO

val databaseModule = module {
    single {
        DynamoDbAsyncClient.create()
    }

    single<PinDAO> {
        DynamoDBPinDAO(
            get(),
            get(named(TABLE_PINS))
        )
    }
}
