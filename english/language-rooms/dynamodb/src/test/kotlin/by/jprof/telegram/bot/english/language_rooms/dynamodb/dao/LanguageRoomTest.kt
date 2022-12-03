package by.jprof.telegram.bot.english.language_rooms.dynamodb.dao

import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.language_rooms.model.LanguageRoom
import by.jprof.telegram.bot.english.language_rooms.model.Violence
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class LanguageRoomTest {
    @Test
    fun toAttributes() {
        assertEquals(
            attributes,
            languageRoom.toAttributes()
        )
    }

    @Test
    fun toAttributesNoThreadId() {
        assertEquals(
            attributes.toMutableMap().apply { this.remove("threadId"); this["id"] = AttributeValue.builder().s("1").build() },
            languageRoom.copy(threadId = null).toAttributes()
        )
    }

    @Test
    fun toLanguageRoom() {
        assertEquals(
            languageRoom,
            attributes.toLanguageRoom()
        )
    }

    @Test
    fun toLanguageRoomNoThreadId() {
        assertEquals(
            languageRoom.copy(threadId = null),
            attributes.toMutableMap().apply { this.remove("threadId") }.toLanguageRoom()
        )
    }

    private val languageRoom
        get() = LanguageRoom(
            chatId = 1,
            threadId = 101,
            language = Language.ENGLISH,
            violence = Violence.POLITE,
            urbanWordOfTheDay = true,
        )
    private val attributes
        get() = mapOf(
            "id" to AttributeValue.builder().s("1:101").build(),
            "chatId" to AttributeValue.builder().n("1").build(),
            "threadId" to AttributeValue.builder().n("101").build(),
            "language" to AttributeValue.builder().s("ENGLISH").build(),
            "violence" to AttributeValue.builder().s("POLITE").build(),
            "urbanWordOfTheDay" to AttributeValue.builder().bool(true).build(),
        )
}
