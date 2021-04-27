package by.jprof.telegram.bot.votes.dynamodb.dao

import by.jprof.telegram.bot.votes.model.Votes
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class VotesTest {
    @Test
    fun toAttributes() {
        Assertions.assertEquals(attributes, votes.toAttributes())
    }

    @Test
    fun toAttributeValuesNoVotes() {
        Assertions.assertEquals(
            attributes.toMutableMap().apply { this["votes"] = AttributeValue.builder().m(emptyMap()).build() },
            votes.copy(votes = emptyMap()).toAttributes()
        )
    }

    @Test
    fun toVotes() {
        Assertions.assertEquals(
            votes,
            attributes.toVotes()
        )
    }

    @Test
    fun toVotesNoVotes() {
        Assertions.assertEquals(
            votes.copy(votes = emptyMap()),
            attributes.toMutableMap().apply { this.remove("votes") }.toVotes(),
        )
    }

    private val votes
        get() = Votes(
            id = "test",
            options = listOf("+", "-"),
            votes = mapOf(
                "User1" to "+",
                "User2" to "-",
            )
        )
    private val attributes
        get() = mapOf(
            "id" to AttributeValue.builder().s("test").build(),
            "options" to AttributeValue.builder().l(
                AttributeValue.builder().s("+").build(),
                AttributeValue.builder().s("-").build(),
            ).build(),
            "votes" to AttributeValue.builder().m(
                mapOf(
                    "User1" to AttributeValue.builder().s("+").build(),
                    "User2" to AttributeValue.builder().s("-").build(),
                )
            ).build(),
        )
}
