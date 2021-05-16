package by.jprof.telegram.bot.kotlin.dynamodb.dao

import by.jprof.telegram.bot.kotlin.model.UserStatistics
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant

internal class UserStatisticsTest {
    @Test
    fun toAttributes() {
        Assertions.assertEquals(
            attributes,
            userStatistics.toAttributes()
        )
    }

    @Test
    fun toUserStatistics() {
        Assertions.assertEquals(
            userStatistics,
            attributes.toUserStatistics()
        )
    }

    private val userStatistics
        get() = UserStatistics(
            count = 42,
            lastMention = Instant.ofEpochMilli(1227376823000)
        )

    private val attributes
        get() = mapOf(
            "count" to AttributeValue.builder().n("42").build(),
            "lastMention" to AttributeValue.builder().n("1227376823000").build(),
        )
}
