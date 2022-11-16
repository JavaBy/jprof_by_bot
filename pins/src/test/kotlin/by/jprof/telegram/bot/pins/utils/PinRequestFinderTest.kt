package by.jprof.telegram.bot.pins.utils

import by.jprof.telegram.bot.pins.model.PinDuration
import by.jprof.telegram.bot.pins.model.PinRequest
import dev.inmo.tgbotapi.types.chat.CommonUser
import dev.inmo.tgbotapi.types.chat.GroupChat
import dev.inmo.tgbotapi.types.message.abstracts.CommonGroupContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.abstracts.PossiblyReplyMessage
import dev.inmo.tgbotapi.types.message.abstracts.UnknownMessageType
import dev.inmo.tgbotapi.types.message.content.AudioContent
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.message.textsources.RegularTextSource
import dev.inmo.tgbotapi.types.update.InlineQueryUpdate
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import io.mockk.every
import io.mockk.mockk
import java.time.Duration
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@RiskFeature
@PreviewFeature
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PinRequestFinderTest {
    @ParameterizedTest
    @MethodSource
    fun default(update: Update, expected: PinRequest?) {
        if (expected == null) {
            Assertions.assertNull(PinRequestFinder.DEFAULT(update))
        } else {
            Assertions.assertEquals(
                expected.copy(request = update.data as Message, message = (update.data as PossiblyReplyMessage).replyTo),
                PinRequestFinder.DEFAULT(update)
            )
        }
    }

    fun default(): Stream<Arguments>? {
        val user = mockk<CommonUser>()
        val chat = mockk<GroupChat>()
        val request = mockk<Message>()
        val replyTo = mockk<Message>()

        return Stream.of(
            Arguments.of(
                mockk<InlineQueryUpdate>(),
                null
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<UnknownMessageType>()
                },
                null
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<ContentMessage<AudioContent>> {
                        every { content } returns mockk()
                    }
                },
                null
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<ContentMessage<TextContent>> {
                        every { content } returns TextContent(
                            text = "pin",
                            textSources = listOf(RegularTextSource("pin"))
                        )
                    }
                },
                null
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin",
                            textSources = listOf(BotCommandTextSource("/pin"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns null
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(Duration.ofHours(1)),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin",
                            textSources = listOf(BotCommandTextSource("/pin"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(Duration.ofHours(1)),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin 2 hours",
                            textSources = listOf(BotCommandTextSource("/pin"), RegularTextSource(" 2 hours"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(Duration.ofHours(2)),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin 1 day",
                            textSources = listOf(BotCommandTextSource("/pin"), RegularTextSource(" 1 day"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(Duration.ofDays(1)),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin 4000 seconds",
                            textSources = listOf(BotCommandTextSource("/pin"), RegularTextSource(" 4000 seconds"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(Duration.ofSeconds(4000)),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin 3 week",
                            textSources = listOf(BotCommandTextSource("/pin"), RegularTextSource(" 3 week"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(Duration.ofDays(21)),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin 2.5 hours",
                            textSources = listOf(BotCommandTextSource("/pin"), RegularTextSource(" 2.5 hours"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(Duration.ofHours(2) + Duration.ofMinutes(30)),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin 1 неделя 3 дня и 14 часов 42 минут",
                            textSources = listOf(BotCommandTextSource("/pin"), RegularTextSource(" 1 неделя 3 дня и 14 часов 42 минут"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(
                        Duration.ofDays(7) + Duration.ofDays(3) + Duration.ofHours(14) + Duration.ofMinutes(42)
                    ),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin 89 дней",
                            textSources = listOf(BotCommandTextSource("/pin"), RegularTextSource(" 89 дней"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Recognized(Duration.ofDays(89)),
                )
            ),
            Arguments.of(
                mockk<MessageUpdate> {
                    every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                        every { content } returns TextContent(
                            text = "/pin blah",
                            textSources = listOf(BotCommandTextSource("/pin"), RegularTextSource(" blah"))
                        )
                        every { this@message.user } returns user
                        every { this@message.chat } returns chat
                        every { this@message.replyTo } returns replyTo
                    }
                },
                PinRequest(
                    message = null,
                    user = user,
                    chat = chat,
                    request = request,
                    duration = PinDuration.Unrecognized,
                )
            ),
        )
    }
}
