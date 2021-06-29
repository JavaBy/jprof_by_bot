package by.jprof.telegram.bot.quizoji.dynamodb.dao

import by.jprof.telegram.bot.quizoji.model.Quizoji
import dev.inmo.tgbotapi.types.MessageEntity.textsources.RegularTextSource
import dev.inmo.tgbotapi.types.message.content.TextContent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class QuizojiTest {
    @Test
    fun toAttributes() {
        Assertions.assertEquals(
            attributes,
            quizoji.toAttributes()
        )
    }

    @Test
    fun toQuizoji() {
        Assertions.assertEquals(
            quizoji,
            attributes.toQuizoji()
        )
    }

    private val quizoji
        get() = Quizoji(
            id = "test",
            question = TextContent(
                text = "Choose the door",
                textSources = listOf(RegularTextSource("Choose the door")),
            ),
        )
    private val attributes
        get() = mapOf(
            "id" to AttributeValue.builder().s("test").build(),
            "question" to AttributeValue.builder()
                .s("{\"type\":\"TextContent\",\"text\":\"Choose the door\",\"textSources\":[{\"type\":\"regular\",\"value\":{\"source\":\"Choose the door\"}}]}")
                .build(),
        )
}
