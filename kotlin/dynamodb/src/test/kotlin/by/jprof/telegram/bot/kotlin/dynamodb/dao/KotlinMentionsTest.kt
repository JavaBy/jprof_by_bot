package by.jprof.telegram.bot.kotlin.dynamodb.dao

import by.jprof.telegram.bot.kotlin.model.KotlinMentions
import by.jprof.telegram.bot.kotlin.model.UserStatistics
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant

internal class KotlinMentionsTest {
    @Test
    fun toAttributes() {
        Assertions.assertEquals(
            attributes,
            kotlinMentions.toAttributes(),
        )
    }

    @Test
    fun tousersStatistics() {
        Assertions.assertEquals(
            kotlinMentions,
            attributes.toKotlinMentions(),
        )
    }

    private val kotlinMentions
        get() = KotlinMentions(
            chat = 42L,
            lastMention = Instant.ofEpochMilli(1227376823000),
            usersStatistics = mapOf(
                1L to UserStatistics(2, Instant.ofEpochMilli(1227376823000)),
                2L to UserStatistics(1, Instant.ofEpochMilli(1214996810000)),
            )
        )

    private val attributes
        get() = mapOf(
            "chat" to AttributeValue.builder().n("42").build(),
            "lastMention" to AttributeValue.builder().n("1227376823000").build(),
            "usersStatistics" to AttributeValue.builder().m(
                mapOf(
                    "1" to AttributeValue.builder().m(
                        mapOf(
                            "count" to AttributeValue.builder().n("2").build(),
                            "lastMention" to AttributeValue.builder().n("1227376823000").build(),
                        )
                    ).build(),
                    "2" to AttributeValue.builder().m(
                        mapOf(
                            "count" to AttributeValue.builder().n("1").build(),
                            "lastMention" to AttributeValue.builder().n("1214996810000").build(),
                        )
                    ).build(),
                )
            ).build(),
        )
}
