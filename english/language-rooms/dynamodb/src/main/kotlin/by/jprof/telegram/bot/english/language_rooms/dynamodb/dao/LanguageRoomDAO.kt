package by.jprof.telegram.bot.english.language_rooms.dynamodb.dao

import by.jprof.telegram.bot.english.language_rooms.dao.LanguageRoomDAO
import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.language_rooms.model.LanguageRoom
import by.jprof.telegram.bot.english.language_rooms.model.Violence
import by.jprof.telegram.bot.utils.dynamodb.toAttributeValue
import by.jprof.telegram.bot.utils.dynamodb.toBoolean
import by.jprof.telegram.bot.utils.dynamodb.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class LanguageRoomDAO(
    private val dynamoDb: DynamoDbAsyncClient,
    private val table: String
) : LanguageRoomDAO {
    override suspend fun save(languageRoom: LanguageRoom) {
        withContext(Dispatchers.IO) {
            dynamoDb.putItem {
                it.tableName(table)
                it.item(languageRoom.toAttributes())
            }.await()
        }
    }

    override suspend fun get(chatId: Long, threadId: Long?): LanguageRoom? {
        return withContext(Dispatchers.IO) {
            dynamoDb.getItem {
                it.tableName(table)
                it.key(
                    mapOf(
                        "id" to id(chatId, threadId).toAttributeValue()
                    )
                )
            }.await()?.item()?.takeUnless { it.isEmpty() }?.toLanguageRoom()
        }
    }

    override suspend fun getAll(): List<LanguageRoom> {
        return withContext(Dispatchers.IO) {
            dynamoDb.scan {
                it.tableName(table)
            }.await()?.items()?.map { it.toLanguageRoom() } ?: emptyList()
        }
    }

    override suspend fun delete(chatId: Long, threadId: Long?) {
        return withContext(Dispatchers.IO) {
            dynamoDb.deleteItem {
                it.tableName(table)
                it.key(
                    mapOf(
                        "id" to id(chatId, threadId).toAttributeValue()
                    )
                )
            }.await()
        }
    }
}

fun id(chatId: Long, threadId: Long?): String = "$chatId${if (threadId != null) ":$threadId" else ""}"

val LanguageRoom.id: String
    get() = id(chatId, threadId)

fun LanguageRoom.toAttributes(): Map<String, AttributeValue> = buildMap {
    put("id", this@toAttributes.id.toAttributeValue())
    put("chatId", this@toAttributes.chatId.toAttributeValue())
    this@toAttributes.threadId?.let {
        put("threadId", it.toAttributeValue())
    }
    put("language", this@toAttributes.language.name.toAttributeValue())
    put("violence", this@toAttributes.violence.name.toAttributeValue())
    put("urbanWordOfTheDay", this@toAttributes.urbanWordOfTheDay.toAttributeValue())
}

fun Map<String, AttributeValue>.toLanguageRoom(): LanguageRoom = LanguageRoom(
    chatId = this["chatId"].toLong("chatId"),
    threadId = this["threadId"]?.n()?.toLong(),
    language = Language.valueOf(this["language"]?.s() ?: throw IllegalStateException("Missing language property")),
    violence = Violence.valueOf(this["violence"]?.s() ?: throw IllegalStateException("Missing violence property")),
    urbanWordOfTheDay = this["urbanWordOfTheDay"].toBoolean("urbanWordOfTheDay"),
)
