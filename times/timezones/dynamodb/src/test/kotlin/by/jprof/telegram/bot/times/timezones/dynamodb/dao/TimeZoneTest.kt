package by.jprof.telegram.bot.times.timezones.dynamodb.dao

import by.jprof.telegram.bot.times.timezones.model.TimeZone
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class TimeZoneTest {
    @Test
    fun toAttributes() {
        Assertions.assertEquals(
            attributes,
            timeZone.toAttributes()
        )
    }

    @Test
    fun toAttributesNoUsername() {
        Assertions.assertEquals(
            attributes.toMutableMap().apply { this.remove("username") },
            timeZone.copy(username = null).toAttributes()
        )
    }

    @Test
    fun toAttributesNoZoneId() {
        Assertions.assertEquals(
            attributes.toMutableMap().apply { this.remove("zoneId") },
            timeZone.copy(zoneId = null).toAttributes()
        )
    }

    @Test
    fun toAttributesNoOffset() {
        Assertions.assertEquals(
            attributes.toMutableMap().apply { this.remove("offset") },
            timeZone.copy(offset = null).toAttributes()
        )
    }

    @Test
    fun toTimeZone() {
        Assertions.assertEquals(
            timeZone,
            attributes.toTimeZone()
        )
    }

    @Test
    fun toTimeZoneNoUsername() {
        Assertions.assertEquals(
            timeZone.copy(username = null),
            attributes.toMutableMap().apply { this.remove("username") }.toTimeZone(),
        )
    }

    @Test
    fun toTimeZoneNoZoneId() {
        Assertions.assertEquals(
            timeZone.copy(zoneId = null),
            attributes.toMutableMap().apply { this.remove("zoneId") }.toTimeZone(),
        )
    }

    @Test
    fun toTimeZoneNoOffset() {
        Assertions.assertEquals(
            timeZone.copy(offset = null),
            attributes.toMutableMap().apply { this.remove("offset") }.toTimeZone(),
        )
    }

    private val timeZone
        get() = TimeZone(
            user = 1L,
            username = "test",
            chat = 2L,
            zoneId = "UTC",
            offset = 3600,
        )
    private val attributes
        get() = mapOf(
            "user" to AttributeValue.builder().n("1").build(),
            "username" to AttributeValue.builder().s("test").build(),
            "chat" to AttributeValue.builder().n("2").build(),
            "zoneId" to AttributeValue.builder().s("UTC").build(),
            "offset" to AttributeValue.builder().n("3600").build(),
        )
}
