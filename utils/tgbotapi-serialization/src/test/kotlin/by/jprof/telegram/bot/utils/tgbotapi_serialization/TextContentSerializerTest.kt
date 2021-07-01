package by.jprof.telegram.bot.utils.tgbotapi_serialization

import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.MessageEntity.textsources.RegularTextSource
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TextContentSerializerTest {
    private val json = Json { serializersModule = TgBotAPI.module }
    private val textContent = TextContent(
        text = "/start up",
        textSources = listOf(
            BotCommandTextSource(source = "/start"),
            RegularTextSource(source = " up")
        )
    )
    private val serialized =
        "{\"type\":\"TextContent\",\"text\":\"/start up\",\"textSources\":[{\"type\":\"bot_command\",\"value\":{\"source\":\"/start\"}},{\"type\":\"regular\",\"value\":{\"source\":\" up\"}}]}"

    @Test
    fun serialize() {
        Assertions.assertEquals(
            serialized,
            json.encodeToString<MessageContent>(textContent)
        )
    }

    @Test
    fun deserialize() {
        Assertions.assertEquals(
            textContent,
            json.decodeFromString<MessageContent>(serialized)
        )
    }
}
