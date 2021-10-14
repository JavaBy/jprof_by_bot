package by.jprof.telegram.bot.monies.dynamodb.dao

import by.jprof.telegram.bot.monies.model.Money
import by.jprof.telegram.bot.monies.model.Monies
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class MoniesTest {
    @Test
    fun toAttributes() {
        Assertions.assertEquals(
            attributes,
            monies.toAttributes()
        )
    }

    @Test
    fun toAttributesNoMonies() {
        Assertions.assertEquals(
            attributes.toMutableMap().apply { this["monies"] = AttributeValue.builder().m(emptyMap()).build() },
            monies.copy(monies = emptyMap()).toAttributes()
        )
    }

    @Test
    fun toMonies() {
        Assertions.assertEquals(
            monies,
            attributes.toMonies()
        )
    }

    @Test
    fun toMoniesNoVotes() {
        Assertions.assertEquals(
            monies.copy(monies = emptyMap()),
            attributes.toMutableMap().apply { this.remove("monies") }.toMonies(),
        )
    }

    private val monies
        get() = Monies(
            user = 1L,
            chat = 2L,
            monies = mapOf(
                Money.PINS to 3
            )
        )
    private val attributes
        get() = mapOf(
            "user" to AttributeValue.builder().n("1").build(),
            "chat" to AttributeValue.builder().n("2").build(),
            "monies" to AttributeValue.builder().m(
                mapOf(
                    "PINS" to AttributeValue.builder().n("3").build(),
                )
            ).build(),
        )
}
