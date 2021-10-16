package by.jprof.telegram.bot.pins.dynamodb.dao

import by.jprof.telegram.bot.pins.dao.PinDAO
import by.jprof.telegram.bot.pins.model.Pin
import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class PinDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : PinDAO {
    override suspend fun save(pin: Pin) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(pin.toAttributes())
            }.await()
        }
    }

    override suspend fun get(chatId: Long, messageId: Long): Pin? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(mapOf(
                    "messageId" to messageId.toAttributeValue(),
                    "chatId" to chatId.toAttributeValue()
                ))
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toPin()
        }
    }

    override suspend fun findByChatId(chatId: Long): List<Pin> {
        return withContext(Dispatchers.IO) {
            dynamoDb.query {
                it.tableName(table)
                it.indexName("chatId")
                it.keyConditionExpression("chatId = :chatId")
                it.expressionAttributeValues(
                    mapOf(
                        ":chatId" to chatId.toAttributeValue()
                    )
                )
            }.await()?.items()?.map { it.toPin() } ?: emptyList()
        }
    }

    override suspend fun findByUserId(userId: Long): List<Pin> {
        return withContext(Dispatchers.IO) {
            dynamoDb.query {
                it.tableName(table)
                it.indexName("userId")
                it.keyConditionExpression("userId = :userId")
                it.expressionAttributeValues(
                    mapOf(
                        ":userId" to userId.toAttributeValue()
                    )
                )
            }.await()?.items()?.map { it.toPin() } ?: emptyList()
        }
    }

    override suspend fun delete(chatId: Long, messageId: Long) {
        withContext(Dispatchers.IO) {
            dynamoDb.deleteItem {
                it.tableName(table)
                it.key(mapOf(
                    "chatId" to chatId.toAttributeValue(),
                    "messageId" to messageId.toAttributeValue()
                ))
            }.await()
        }
    }
}

fun Pin.toAttributes(): Map<String, AttributeValue> = mapOf(
    "chatId" to this.chatId.toAttributeValue(),
    "messageId" to this.messageId.toAttributeValue(),
    "userId" to this.userId.toAttributeValue(),
)

fun Map<String, AttributeValue>.toPin(): Pin = Pin(
    chatId = this["chatId"].toLong("chatId"),
    messageId = this["messageId"].toLong("messageId"),
    userId = this["userId"].toLong("userId"),
)
