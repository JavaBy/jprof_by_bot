package by.jprof.telegram.bot.pins.dynamodb.dao

import by.jprof.telegram.bot.pins.model.Pin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class PinTest {
    @Test
    fun toAttributes() {
        Assertions.assertEquals(
            attributes,
            pin.toAttributes()
        )
    }

    @Test
    fun toPin() {
        Assertions.assertEquals(
            pin,
            attributes.toPin()
        )
    }

    private val pin
        get() = Pin(
            chatId = 1,
            messageId = 1,
            userId = 1,
        )
    private val attributes
        get() = mapOf(
            "chatId" to AttributeValue.builder().n("1").build(),
            "messageId" to AttributeValue.builder().n("1").build(),
            "userId" to AttributeValue.builder().n("1").build(),
        )
}
