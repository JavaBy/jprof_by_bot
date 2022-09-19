package by.jprof.telegram.bot.times.utils

import by.jprof.telegram.bot.times.model.TimeZoneRequest
import by.jprof.telegram.bot.times.model.TimeZoneValue
import dev.inmo.tgbotapi.types.CommonUser
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.MessageEntity.textsources.RegularTextSource
import dev.inmo.tgbotapi.types.chat.abstracts.GroupChat
import dev.inmo.tgbotapi.types.message.abstracts.CommonGroupContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.abstracts.UnknownMessageType
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.media.AudioContent
import dev.inmo.tgbotapi.types.update.InlineQueryUpdate
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.RiskFeature
import io.mockk.every
import io.mockk.mockk
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@RiskFeature
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TimeZoneParserTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource
    fun default(update: Update, expected: TimeZoneRequest?) {
        if (expected == null) {
            Assertions.assertNull(TimeZoneParser.DEFAULT(update))
        } else {
            Assertions.assertEquals(
                expected.copy(request = update.data as Message),
                TimeZoneParser.DEFAULT(update)
            )
        }
    }

    fun default(): Stream<Arguments>? {
        val user = mockk<CommonUser>()
        val chat = mockk<GroupChat>()
        val request = mockk<Message>()

        return Stream.of(
            Arguments.of(
                Named.of("Only updates with a message are supported", mockk<InlineQueryUpdate>()),
                null,
            ),
            Arguments.of(
                Named.of(
                    "Only content messages are supported",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<UnknownMessageType>()
                    }
                ),
                null,
            ),
            Arguments.of(
                Named.of(
                    "Only text content messages are supported",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<ContentMessage<AudioContent>> {
                            every { content } returns mockk()
                        }
                    }
                ),
                null,
            ),
            Arguments.of(
                Named.of(
                    "The parser expects a command",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<ContentMessage<TextContent>> {
                            every { content } returns TextContent(
                                text = "tz",
                                textSources = listOf(RegularTextSource("tz"))
                            )
                        }
                    },
                ),
                null,
            ),
            Arguments.of(
                Named.of(
                    "The parser expects a /tz command",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<ContentMessage<TextContent>> {
                            every { content } returns TextContent(
                                text = "/other",
                                textSources = listOf(BotCommandTextSource("/other"))
                            )
                        }
                    },
                ),
                null,
            ),
            Arguments.of(
                Named.of(
                    "Empty /tz command should produce an empty TimeZoneRequest",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz",
                                textSources = listOf(BotCommandTextSource("/tz"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Empty,
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with an unrecognized parameter should produce an unrecognized TimeZoneRequest",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz foobar",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" foobar"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Unrecognized,
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid TZ ID should produce a TimeZoneRequest with this TZ ID",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz UTC",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" UTC"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Id("UTC"),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid TZ ID should produce a TimeZoneRequest with this TZ ID",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz Europe/Berlin",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" Europe/Berlin"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    }
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Id("Europe/Berlin"),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid TZ ID should produce a TimeZoneRequest with this TZ ID",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz PST",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" PST"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    }
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Id("America/Los_Angeles"),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid TZ ID should produce a TimeZoneRequest with this TZ ID",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz +03:00",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" +03:00"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Id("+03:00"),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid TZ ID should produce a TimeZoneRequest with this TZ ID",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz -05:00",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" -05:00"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Id("-05:00"),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid TZ ID should produce a TimeZoneRequest with this TZ ID",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz GMT",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" GMT"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Id("GMT"),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid TZ ID should produce a TimeZoneRequest with this TZ ID",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz GMT+3",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" GMT+3"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Id("GMT+03:00"),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid TZ ID should produce a TimeZoneRequest with this TZ ID",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz +01:23",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" +01:23"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Id("+01:23"),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid offset should produce a TimeZoneRequest with this offset",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz 3600",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" 3600"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Offset(3600),
                ),
            ),
            Arguments.of(
                Named.of(
                    "A /tz command with a valid offset should produce a TimeZoneRequest with this offset",
                    mockk<MessageUpdate> {
                        every { data } returns mockk<CommonGroupContentMessage<TextContent>> message@{
                            every { content } returns TextContent(
                                text = "/tz -7200",
                                textSources = listOf(BotCommandTextSource("/tz"), RegularTextSource(" -7200"))
                            )
                            every { this@message.user } returns user
                            every { this@message.chat } returns chat
                        }
                    },
                ),
                TimeZoneRequest(
                    user = user,
                    chat = chat,
                    request = request,
                    value = TimeZoneValue.Offset(-7200),
                ),
            ),
        )
    }
}
