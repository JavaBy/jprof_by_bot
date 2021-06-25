package by.jprof.telegram.bot.dialogs.dynamodb.dao

import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForQuestion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class DialogStateTest {
    @Test
    fun waitingForQuestionToAttributes() {
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
    fun attributesToWaitingForQuestionTo() {
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
}
