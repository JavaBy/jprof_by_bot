package by.jprof.telegram.bot.utils.dynamodb

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class ExtensionsTest {
    @Test
    fun stringToAttributeValue() {
        Assertions.assertEquals(
            AttributeValue.builder().s("test").build(),
            "test".toAttributeValue()
        )
    }

    @Test
    fun listToAttributeValue() {
        Assertions.assertEquals(
            AttributeValue.builder().l(
                AttributeValue.builder().s("test1").build(),
                AttributeValue.builder().s("test2").build(),
            ).build(),
            listOf(
                "test1".toAttributeValue(),
                "test2".toAttributeValue(),
            ).toAttributeValue()
        )
    }

    @Test
    fun mapToAttributeValue() {
        Assertions.assertEquals(
            AttributeValue.builder().m(
                mapOf(
                    "test1" to AttributeValue.builder().s("test1").build(),
                    "test2" to AttributeValue.builder().s("test2").build(),
                )
            ).build(),
            mapOf(
                "test1" to "test1".toAttributeValue(),
                "test2" to "test2".toAttributeValue(),
            ).toAttributeValue()
        )
    }
}
