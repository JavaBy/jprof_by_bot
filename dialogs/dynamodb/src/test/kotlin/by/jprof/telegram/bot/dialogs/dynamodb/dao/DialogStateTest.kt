package by.jprof.telegram.bot.dialogs.dynamodb.dao

import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForOptions
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForQuestion
import dev.inmo.tgbotapi.types.message.content.TextContent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class DialogStateTest {
    @Test
    fun quizojiWaitingForQuestionToAttributes() {
        Assertions.assertEquals(
            mapOf(
                "userId" to AttributeValue.builder().n("2").build(),
                "chatId" to AttributeValue.builder().n("1").build(),
                "value" to AttributeValue
                    .builder()
                    .s("{\"type\":\"WaitingForQuestion\",\"chatId\":1,\"userId\":2}")
                    .build(),
            ),
            WaitingForQuestion(1, 2).toAttributes()
        )
    }

    @Test
    fun quizojiAttributesToWaitingForQuestion() {
        Assertions.assertEquals(
            WaitingForQuestion(1, 2),
            mapOf(
                "userId" to AttributeValue.builder().n("2").build(),
                "chatId" to AttributeValue.builder().n("1").build(),
                "value" to AttributeValue
                    .builder()
                    .s("{\"type\":\"WaitingForQuestion\",\"chatId\":1,\"userId\":2}")
                    .build(),
            ).toDialogState()
        )
    }

    @Test
    fun quizojiWaitingForOptionsToAttributes() {
        Assertions.assertEquals(
            mapOf(
                "userId" to AttributeValue.builder().n("2").build(),
                "chatId" to AttributeValue.builder().n("1").build(),
                "value" to AttributeValue
                    .builder()
                    .s("{\"type\":\"WaitingForOptions\",\"chatId\":1,\"userId\":2,\"question\":{\"type\":\"TextContent\",\"text\":\"Question\"}}")
                    .build(),
            ),
            WaitingForOptions(1, 2, TextContent("Question")).toAttributes()
        )
    }

    @Test
    fun quizojiAttributesToWaitingForOptions() {
        Assertions.assertEquals(
            WaitingForOptions(1, 2, TextContent("Question")),
            mapOf(
                "userId" to AttributeValue.builder().n("2").build(),
                "chatId" to AttributeValue.builder().n("1").build(),
                "value" to AttributeValue
                    .builder()
                    .s("{\"type\":\"WaitingForOptions\",\"chatId\":1,\"userId\":2,\"question\":{\"type\":\"TextContent\",\"text\":\"Question\"}}")
                    .build(),
            ).toDialogState()
        )
    }
}
