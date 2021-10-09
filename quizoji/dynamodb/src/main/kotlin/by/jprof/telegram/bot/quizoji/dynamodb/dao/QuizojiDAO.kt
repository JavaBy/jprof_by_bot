package by.jprof.telegram.bot.quizoji.dynamodb.dao

import by.jprof.telegram.bot.quizoji.dao.QuizojiDAO
import by.jprof.telegram.bot.quizoji.model.Quizoji
import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toString
import by.jprof.telegram.bot.utils.tgbotapi_serialization.TgBotAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@ExperimentalSerializationApi
class QuizojiDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : QuizojiDAO {
    override suspend fun save(quizoji: Quizoji) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(quizoji.toAttributes())
            }.await()
        }
    }

    override suspend fun get(id: String): Quizoji? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(mapOf("id" to id.toAttributeValue()))
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toQuizoji()
        }
    }
}

private val json = Json { serializersModule = TgBotAPI.module }

@ExperimentalSerializationApi
fun Quizoji.toAttributes(): Map<String, AttributeValue> = mapOf(
    "id" to this.id.toAttributeValue(),
    "question" to json.encodeToString(this.question).toAttributeValue(),
)

@ExperimentalSerializationApi
fun Map<String, AttributeValue>.toQuizoji(): Quizoji = Quizoji(
    id = this["id"].toString("id"),
    question = json.decodeFromString(this["question"].toString("value")),
)
