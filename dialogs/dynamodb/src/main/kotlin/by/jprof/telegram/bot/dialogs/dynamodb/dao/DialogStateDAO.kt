package by.jprof.telegram.bot.dialogs.dynamodb.dao

import by.jprof.telegram.bot.dialogs.dao.DialogStateDAO
import by.jprof.telegram.bot.dialogs.model.DialogState
import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class DialogStateDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : DialogStateDAO {
    override suspend fun get(chatId: Long, userId: Long): DialogState? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(
                    mapOf(
                        "userId" to userId.toAttributeValue(),
                        "chatId" to chatId.toAttributeValue(),
                    )
                )
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toDialogState()
        }
    }

    override suspend fun save(dialogState: DialogState) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(dialogState.toAttributes())
            }.await()
        }
    }

    override suspend fun delete(chatId: Long, userId: Long) {
        withContext(Dispatchers.IO) {
            dynamoDb.deleteItem {
                it.tableName(table)
                it.key(
                    mapOf(
                        "userId" to userId.toAttributeValue(),
                        "chatId" to chatId.toAttributeValue(),
                    )
                )
            }
        }
    }
}

private val json = Json { serializersModule = DialogState.serializers }

fun Map<String, AttributeValue>.toDialogState(): DialogState = json.decodeFromString(this["value"].toString("value"))

fun DialogState.toAttributes(): Map<String, AttributeValue> = mapOf(
    "userId" to this.userId.toAttributeValue(),
    "chatId" to this.chatId.toAttributeValue(),
    "value" to json.encodeToString(this).toAttributeValue(),
)
