package by.jprof.telegram.bot.votes.tgbotapi_extensions

import by.jprof.telegram.bot.votes.model.Votes
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VotesExtensionsTest {
    @ParameterizedTest
    @MethodSource
    fun toInlineKeyboardMarkup(votes: Votes, keyboard: InlineKeyboardMarkup) {
        Assertions.assertEquals(keyboard, votes.toInlineKeyboardMarkup())
    }

    private fun toInlineKeyboardMarkup(): Stream<Arguments> = sequence {
        yield(
            Arguments.of(
                Votes("test"),
                InlineKeyboardMarkup(
                    listOf(emptyList())
                )
            )
        )
        yield(
            Arguments.of(
                Votes("test", listOf("1")),
                InlineKeyboardMarkup(
                    listOf(
                        listOf(
                            CallbackDataInlineKeyboardButton("0 1", "test:1")
                        )
                    )
                )
            )
        )
        yield(
            Arguments.of(
                Votes("test", listOf("1", "2")),
                InlineKeyboardMarkup(
                    listOf(
                        listOf(
                            CallbackDataInlineKeyboardButton("0 1", "test:1"),
                            CallbackDataInlineKeyboardButton("0 2", "test:2"),
                        )
                    )
                )
            )
        )
        yield(
            Arguments.of(
                Votes(
                    "test",
                    listOf("1", "2", "3"),
                    mapOf("user1" to "1", "user2" to "2", "user3" to "UNEXISTING_OPTION")
                ),
                InlineKeyboardMarkup(
                    listOf(
                        listOf(
                            CallbackDataInlineKeyboardButton("1 1", "test:1"),
                            CallbackDataInlineKeyboardButton("1 2", "test:2"),
                            CallbackDataInlineKeyboardButton("0 3", "test:3"),
                        )
                    )
                )
            )
        )
    }.asStream()
}
