package by.jprof.telegram.bot.english.urban_word_of_the_day.dynamodb.dao

import by.jprof.telegram.bot.english.urban_word_of_the_day.dao.UrbanWordOfTheDayDAO
import by.jprof.telegram.bot.english.urban_word_of_the_day.model.UrbanWordOfTheDay
import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toString
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class UrbanWordOfTheDayDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : UrbanWordOfTheDayDAO {
    override suspend fun save(urbanWordOfTheDay: UrbanWordOfTheDay) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(urbanWordOfTheDay.toAttributes())
            }.await()
        }
    }

    override suspend fun get(date: LocalDate): UrbanWordOfTheDay? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(
                    mapOf(
                        "date" to date.toString().toAttributeValue()
                    )
                )
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toUrbanWordOfTheDay()
        }
    }
}

fun UrbanWordOfTheDay.toAttributes(): Map<String, AttributeValue> = buildMap {
    put("date", this@toAttributes.date.toString().toAttributeValue())
    put("word", this@toAttributes.word.toAttributeValue())
    put("definition", this@toAttributes.definition.toAttributeValue())
    this@toAttributes.example?.let {
        put("example", it.toAttributeValue())
    }
    put("permalink", this@toAttributes.permalink.toAttributeValue())
}

fun Map<String, AttributeValue>.toUrbanWordOfTheDay(): UrbanWordOfTheDay = UrbanWordOfTheDay(
    date = LocalDate.parse(this["date"].toString("date")),
    word = this["word"].toString("word"),
    definition = this["definition"].toString("definition"),
    example = this["example"]?.s(),
    permalink = this["permalink"].toString("permalink"),
)
