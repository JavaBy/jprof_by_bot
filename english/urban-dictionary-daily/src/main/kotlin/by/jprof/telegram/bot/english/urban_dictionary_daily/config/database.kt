package by.jprof.telegram.bot.english.urban_dictionary_daily.config

import by.jprof.telegram.bot.english.language_rooms.dao.LanguageRoomDAO
import by.jprof.telegram.bot.english.urban_word_of_the_day.dao.UrbanWordOfTheDayDAO
import org.koin.core.qualifier.named
import org.koin.dsl.module
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import by.jprof.telegram.bot.english.language_rooms.dynamodb.dao.LanguageRoomDAO as DynamoDBLanguageRoomDAO
import by.jprof.telegram.bot.english.urban_word_of_the_day.dynamodb.dao.UrbanWordOfTheDayDAO as DynamoDBUrbanWordOfTheDayDAO

val databaseModule = module {
    single {
        DynamoDbAsyncClient.create()
    }

    single<UrbanWordOfTheDayDAO> {
        DynamoDBUrbanWordOfTheDayDAO(
            get(),
            get(named(TABLE_URBAN_WORDS_OF_THE_DAY))
        )
    }

    single<LanguageRoomDAO> {
        DynamoDBLanguageRoomDAO(
            get(),
            get(named(TABLE_LANGUAGE_ROOMS))
        )
    }
}
