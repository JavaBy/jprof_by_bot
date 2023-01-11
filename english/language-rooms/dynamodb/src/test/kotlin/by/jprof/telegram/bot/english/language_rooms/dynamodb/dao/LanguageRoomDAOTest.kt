package by.jprof.telegram.bot.english.language_rooms.dynamodb.dao

import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.language_rooms.model.LanguageRoom
import by.jprof.telegram.bot.english.language_rooms.model.Violence
import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import kotlinx.coroutines.runBlocking
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class LanguageRoomDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: LanguageRoomDAO

    @BeforeAll
    internal fun setup() {
        sut = LanguageRoomDAO(dynamoDB, "languageRooms")
    }

    @Test
    fun save() = runBlocking {
        sut.save(languageRoom.copy(chatId = 11))
    }

    @Test
    fun saveNoThreadId() = runBlocking {
        sut.save(languageRoom.copy(chatId = 12, threadId = null))
    }

    @Test
    fun get() = runBlocking {
        assertEquals(languageRoom, sut.get(1, 101))
    }

    @Test
    fun getNoThreadId() = runBlocking {
        assertEquals(languageRoom.copy(chatId = 2, threadId = null), sut.get(2))
    }

    @Test
    fun getUnexisting() = runBlocking {
        assertNull(sut.get(-1, -2))
        assertNull(sut.get(-2))
    }

    @Test
    fun getAll() = runBlocking {
        val all = sut.getAll()

        assertTrue(all.contains(languageRoom))
        assertTrue(all.contains(languageRoom.copy(chatId = 2, threadId = null)))
    }

    @Test
    fun delete() = runBlocking {
        sut.save(languageRoom.copy(chatId = 21))
        assertEquals(languageRoom.copy(chatId = 21), sut.get(21, 101))
        sut.delete(21, 101)
        assertNull(sut.get(21, 101))

        sut.save(languageRoom.copy(chatId = 22, threadId = null))
        assertEquals(languageRoom.copy(chatId = 22, threadId = null), sut.get(22))
        sut.delete(22)
        assertNull(sut.get(22))
    }

    private val languageRoom
        get() = LanguageRoom(
            chatId = 1,
            threadId = 101,
            language = Language.ENGLISH,
            violence = Violence.POLITE,
            urbanWordOfTheDay = true,
        )
}
