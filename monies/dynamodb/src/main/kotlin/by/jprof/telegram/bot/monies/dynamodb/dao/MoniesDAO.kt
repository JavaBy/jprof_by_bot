package by.jprof.telegram.bot.monies.dynamodb.dao

import by.jprof.telegram.bot.monies.dao.MoniesDAO
import by.jprof.telegram.bot.monies.model.Money
import by.jprof.telegram.bot.monies.model.Monies
import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class MoniesDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : MoniesDAO {
    override suspend fun save(monies: Monies) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(monies.toAttributes())
            }.await()
        }
    }

    override suspend fun get(user: Long, chat: Long): Monies? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(
                    mapOf(
                        "user" to user.toAttributeValue(),
                        "chat" to chat.toAttributeValue(),
                    )
                )
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toMonies()
        }
    }
}

fun Monies.toAttributes(): Map<String, AttributeValue> = mapOf(
    "user" to this.user.toAttributeValue(),
    "chat" to this.chat.toAttributeValue(),
    "monies" to this
        .monies
        .mapKeys { (key, _) -> key.name }
        .mapValues { (_, value) -> value.toAttributeValue() }
        .toAttributeValue()
)

fun Map<String, AttributeValue>.toMonies(): Monies = Monies(
    user = this["user"].toLong("user"),
    chat = this["chat"].toLong("chat"),
    monies = this["monies"]?.m()
        ?.mapValues { (_, value) -> value.n()?.toIntOrNull() ?: 0 }
        ?.mapKeys { (key, _) -> Money.valueOf(key) }
        ?: emptyMap()
)
