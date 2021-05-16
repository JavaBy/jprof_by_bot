package by.jprof.telegram.bot.kotlin.dynamodb.dao

import by.jprof.telegram.bot.kotlin.dao.KotlinMentionsDAO
import by.jprof.telegram.bot.kotlin.model.KotlinMentions
import by.jprof.telegram.bot.kotlin.model.UserStatistics
import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toInstant
import by.jprof.telegram.bot.utils.dynamodb.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class KotlinMentionsDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : KotlinMentionsDAO {
    override suspend fun save(kotlinMentions: KotlinMentions) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(kotlinMentions.toAttributes())
            }.await()
        }
    }

    override suspend fun get(chat: Long): KotlinMentions? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(mapOf("chat" to chat.toAttributeValue()))
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toKotlinMentions()
        }
    }
}

fun UserStatistics.toAttributes(): Map<String, AttributeValue> = mapOf(
    "count" to this.count.toAttributeValue(),
    "lastMention" to this.lastMention.toAttributeValue(),
)

fun KotlinMentions.toAttributes(): Map<String, AttributeValue> = mapOf(
    "chat" to this.chat.toAttributeValue(),
    "lastMention" to this.lastMention.toAttributeValue(),
    "usersStatistics" to this.usersStatistics
        .mapKeys { it.key.toString() }
        .mapValues { AttributeValue.builder().m(it.value.toAttributes()).build() }
        .toAttributeValue()
)

fun Map<String, AttributeValue>.toUserStatistics(): UserStatistics = UserStatistics(
    count = this["count"].toLong("count"),
    lastMention = this["lastMention"].toInstant("lastMention"),
)

fun Map<String, AttributeValue>.toKotlinMentions(): KotlinMentions = KotlinMentions(
    chat = this["chat"].toLong("chat"),
    lastMention = this["lastMention"].toInstant("lastMention"),
    usersStatistics = this["usersStatistics"]?.m()?.let {
        it
            .mapValues { (_, value) -> value.m().toUserStatistics() }
            .mapKeys { (key, _) -> key.toLong() }
    } ?: emptyMap()
)
