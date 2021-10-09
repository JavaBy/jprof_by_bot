package by.jprof.telegram.bot.utils.dynamodb

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant

internal class ExtensionsTest {
    @Test
    fun stringToAttributeValue() {
        Assertions.assertEquals(
            AttributeValue.builder().s("test").build(),
            "test".toAttributeValue()
        )
    }

    @Test
    fun longToAttributeValue() {
        Assertions.assertEquals(
            AttributeValue.builder().n("42").build(),
            42L.toAttributeValue()
        )
    }

    @Test
    fun instantToAttributeValue() {
        Assertions.assertEquals(
            AttributeValue.builder().n("1227376823000").build(),
            Instant.ofEpochMilli(1227376823000).toAttributeValue()
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

    @Test
    fun attributeValueToString() {
        Assertions.assertEquals(
            "test",
            AttributeValue.builder().s("test").build().toString("test")
        )
    }

    @Test
    fun attributeValueToStringMissing() {
        Assertions.assertThrows(
            IllegalStateException::class.java,
            {
                AttributeValue.builder().bool(true).build().toString("test")
            },
            "Missing test property"
        )
    }

    @Test
    fun attributeValueToLong() {
        Assertions.assertEquals(
            42L,
            AttributeValue.builder().n("42").build().toLong("test")
        )
    }

    @Test
    fun attributeValueToLongMissing() {
        Assertions.assertThrows(
            IllegalStateException::class.java,
            {
                AttributeValue.builder().bool(true).build().toLong("test")
            },
            "Missing test property"
        )
    }

    @Test
    fun attributeValueToInstant() {
        Assertions.assertEquals(
            Instant.ofEpochMilli(1227376823000),
            AttributeValue.builder().n("1227376823000").build().toInstant("test")
        )
    }

    @Test
    fun attributeValueToInstantMissing() {
        Assertions.assertThrows(
            IllegalStateException::class.java,
            {
                AttributeValue.builder().bool(true).build().toInstant("test")
            },
            "Missing test property"
        )
    }
}
