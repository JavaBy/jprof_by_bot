package by.jprof.telegram.bot.youtube.dynamodb.dao

import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toString
import by.jprof.telegram.bot.youtube.dao.YouTubeChannelsWhitelistDAO
import by.jprof.telegram.bot.youtube.model.YouTubeChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class YouTubeChannelsWhitelistDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : YouTubeChannelsWhitelistDAO {
    override suspend fun get(id: String): YouTubeChannel? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(mapOf("id" to id.toAttributeValue()))
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toYouTubeChannel()
        }
    }
}

fun Map<String, AttributeValue>.toYouTubeChannel(): YouTubeChannel = YouTubeChannel(
    id = this["id"].toString("id")
)
