package by.jprof.telegram.bot.times.timezones.dynamodb.dao

import by.jprof.telegram.bot.times.timezones.dao.TimeZoneDAO
import by.jprof.telegram.bot.times.timezones.model.TimeZone
import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class TimeZoneDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : TimeZoneDAO {
    override suspend fun save(timeZone: TimeZone) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(timeZone.toAttributes())
            }.await()
        }
    }

    override suspend fun get(user: Long, chat: Long): TimeZone? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(
                    mapOf(
                        "user" to user.toAttributeValue(),
                        "chat" to chat.toAttributeValue(),
                    )
                )
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toTimeZone()
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun TimeZone.toAttributes(): Map<String, AttributeValue> = buildMap {
    put("user", this@toAttributes.user.toAttributeValue())
    put("chat", this@toAttributes.chat.toAttributeValue())
    this@toAttributes.zoneId?.let {
        put("zoneId", it.toAttributeValue())
    }
    this@toAttributes.offset?.let {
        put("offset", it.toAttributeValue())
    }
}

fun Map<String, AttributeValue>.toTimeZone(): TimeZone = TimeZone(
    user = this["user"].toLong("user"),
    chat = this["chat"].toLong("chat"),
    zoneId = this["zoneId"]?.s(),
    offset = this["offset"]?.n()?.toInt()
)
