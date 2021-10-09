package by.jprof.telegram.bot.jep

import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.MessageEntity.textsources.TextLinkTextSource
import dev.inmo.tgbotapi.types.MessageEntity.textsources.URLTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.media.PhotoContent
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.UnknownUpdate
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class JEPUpdateProcessorTest {
    @MockK
    private lateinit var jepSummary: JEPSummary

    @MockK
    private lateinit var votesDAO: VotesDAO

    @MockK(relaxed = true)
    private lateinit var bot: RequestsExecutor

    lateinit var sut: JEPUpdateProcessor

    @BeforeEach
    fun setUp() {
        sut = JEPUpdateProcessor(
            jepSummary = jepSummary,
            votesDAO = votesDAO,
            bot = bot,
        )
    }

    @Test
    fun processUnknownUpdate() = runBlocking {
        sut.process(UnknownUpdate(1L, "", JsonNull))

        verify { listOf(jepSummary, votesDAO, bot) wasNot called }
    }

    @Test
    fun processUnknownMessage() = runBlocking {
        sut.process(MessageUpdate(1L, mockk()))

        verify { listOf(jepSummary, votesDAO, bot) wasNot called }
    }

    @Test
    fun processNonTextMessage() = runBlocking {
        val message = mockk<ContentMessage<PhotoContent>> {
            every { content } returns mockk()
        }

        sut.process(MessageUpdate(1L, message))

        verify { listOf(jepSummary, votesDAO, bot) wasNot called }
    }

    @Test
    fun processMessageWithNoLinks() = runBlocking {
        val message = mockk<ContentMessage<TextContent>> {
            every { content } returns TextContent("Hello, world!")
        }

        sut.process(MessageUpdate(1L, message))

        verify { listOf(jepSummary, votesDAO, bot) wasNot called }
    }

    @Test
    fun processMessageWithNoJEPs() = runBlocking {
        val message = mockk<ContentMessage<TextContent>> {
            every { content } returns TextContent(
                "Hello, world!",
                listOf(
                    URLTextSource("https://google.com"),
                    TextLinkTextSource(" google", "https://google.com")
                )
            )
        }

        sut.process(MessageUpdate(1L, message))

        verify { listOf(jepSummary, votesDAO, bot) wasNot called }
    }

    @Test
    fun processMessageNullJEPSummary() = runBlocking {
        val message = mockk<ContentMessage<TextContent>> {
            every { messageId } returns 1L
            every { content } returns TextContent(
                "Hello, world!",
                listOf(
                    URLTextSource("https://openjdk.java.net/jeps/1"),
                    TextLinkTextSource("JEP 2", "https://openjdk.java.net/jeps/2"),
                )
            )
            every { chat } returns mockk {
                every { id } returns ChatId(1L)
            }
        }

        coEvery { jepSummary.of(any()) } returns null
        coEvery { votesDAO.get(any()) } returns null

        sut.process(MessageUpdate(1L, message))

        coVerify(exactly = 1) {
            bot.execute(
                SendTextMessage(
                    chatId = ChatId(1L),
                    text = "Cast your vote for *JEP 1* now ⤵️",
                    parseMode = MarkdownV2ParseMode,
                    replyToMessageId = 1,
                    replyMarkup = Votes("JEP-1", listOf("\uD83D\uDC4D", "\uD83D\uDC4E")).toInlineKeyboardMarkup()
                )
            )
            bot.execute(
                SendTextMessage(
                    chatId = ChatId(1L),
                    text = "Cast your vote for *JEP 2* now ⤵️",
                    parseMode = MarkdownV2ParseMode,
                    replyToMessageId = 1,
                    replyMarkup = Votes("JEP-2", listOf("\uD83D\uDC4D", "\uD83D\uDC4E")).toInlineKeyboardMarkup()
                )
            )
        }
        confirmVerified(bot)
        clearAllMocks()
    }

    @Test
    fun processMessage() = runBlocking {
        val message = mockk<ContentMessage<TextContent>> {
            every { messageId } returns 1L
            every { content } returns TextContent(
                "Hello, world!",
                listOf(
                    URLTextSource("https://openjdk.java.net/jeps/1"),
                    TextLinkTextSource("JEP 2", "https://openjdk.java.net/jeps/2"),
                )
            )
            every { chat } returns mockk {
                every { id } returns ChatId(1L)
            }
        }

        coEvery { jepSummary.of(any()) } answers { "JEP ${it.invocation.args[0]}!" }
        coEvery { votesDAO.get(any()) } returns null

        sut.process(MessageUpdate(1L, message))

        coVerify(exactly = 1) {
            bot.execute(
                SendTextMessage(
                    chatId = ChatId(1L),
                    text = "JEP 1\\!\n\nCast your vote for *JEP 1* now ⤵️",
                    parseMode = MarkdownV2ParseMode,
                    replyToMessageId = 1,
                    replyMarkup = Votes("JEP-1", listOf("\uD83D\uDC4D", "\uD83D\uDC4E")).toInlineKeyboardMarkup()
                )
            )
            bot.execute(
                SendTextMessage(
                    chatId = ChatId(1L),
                    text = "JEP 2\\!\n\nCast your vote for *JEP 2* now ⤵️",
                    parseMode = MarkdownV2ParseMode,
                    replyToMessageId = 1,
                    replyMarkup = Votes("JEP-2", listOf("\uD83D\uDC4D", "\uD83D\uDC4E")).toInlineKeyboardMarkup()
                )
            )
        }
        confirmVerified(bot)
        clearAllMocks()
    }

    @Test
    fun processUnknownCallbackQuery() = runBlocking {
        sut.process(CallbackQueryUpdate(1L, mockk()))

        verify { listOf(jepSummary, votesDAO, bot) wasNot called }
    }

    @Test
    fun processNonJEPCallbackQuery() = runBlocking {
        sut.process(
            CallbackQueryUpdate(
                1L, MessageDataCallbackQuery(
                id = "",
                user = mockk(),
                chatInstance = "",
                message = mockk(),
                data = "YOUTUBE-1:+",
            )
            )
        )

        verify { listOf(jepSummary, votesDAO, bot) wasNot called }
    }

    @Test
    fun processBadCallbackQuery() = runBlocking {
        sut.process(
            CallbackQueryUpdate(
                1L, MessageDataCallbackQuery(
                id = "",
                user = mockk(),
                chatInstance = "",
                message = mockk(),
                data = "JEP-1+",
            )
            )
        )

        verify { listOf(jepSummary, votesDAO, bot) wasNot called }
    }

    @Test
    @Disabled
    fun processCallbackQuery() = runBlocking {
        val callbackQuery = MessageDataCallbackQuery(
            id = "",
            user = mockk {
                every { id } returns ChatId(1L)
            },
            chatInstance = "",
            message = mockk<ContentMessage<TextContent>> {
                every { messageId } returns 100500L
                every { chat } returns mockk {
                    every { id } returns ChatId(2L)
                }
                every { content } returns TextContent("")
            },
            data = "JEP-1:+",
        )

        coEvery { votesDAO.get("JEP-1") } returns Votes("JEP-1", listOf("+", "-"), mapOf("2" to "-"))
        coEvery { votesDAO.save(any()) } just runs
        coEvery { bot.answerCallbackQuery(callbackQuery) } returns true

        sut.process(CallbackQueryUpdate(1L, callbackQuery))

        coVerify(exactly = 1) { votesDAO.save(Votes("JEP-1", listOf("+", "-"), mapOf("1" to "+", "2" to "-"))) }
        coVerify(exactly = 1) { bot.answerCallbackQuery(callbackQuery) }

        confirmVerified(bot)
        clearAllMocks()
    }
}
