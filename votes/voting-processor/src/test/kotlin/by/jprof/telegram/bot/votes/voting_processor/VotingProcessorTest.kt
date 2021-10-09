package by.jprof.telegram.bot.votes.voting_processor

import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import com.soywiz.klock.DateTime
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.ReplyMarkup.editMessageReplyMarkup
import dev.inmo.tgbotapi.types.CallbackQuery.InlineMessageIdDataCallbackQuery
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.CallbackQuery.MessageGameShortNameCallbackQuery
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.CommonUser
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.chat.GroupChatImpl
import dev.inmo.tgbotapi.types.message.CommonGroupContentMessageImpl
import dev.inmo.tgbotapi.types.message.content.TextContent
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class VotingProcessorTest {
    @MockK
    private lateinit var votesDAO: VotesDAO

    @MockK(relaxed = true)
    private lateinit var bot: RequestsExecutor

    @MockK
    private lateinit var votesConstructor: (String) -> Votes

    lateinit var sut: VotingProcessor

    @BeforeEach
    fun setUp() {
        sut = object : VotingProcessor(
            prefix = "TEST",
            votesDAO = votesDAO,
            votesConstructor = votesConstructor,
            bot = bot,
        ) {}
    }

    @Test
    fun processMessageDataCallbackQuery() = runBlocking {
        val message = CommonGroupContentMessageImpl(
            chat = GroupChatImpl(
                id = ChatId(1L),
                title = "Test"
            ),
            messageId = 1L,
            user = CommonUser(UserId(2L), "Test 2"),
            date = DateTime.now(),
            forwardInfo = null,
            editDate = null,
            replyTo = null,
            replyMarkup = null,
            content = TextContent(""),
            senderBot = null,
        )
        val callbackQuery = MessageDataCallbackQuery(
            id = "",
            user = CommonUser(UserId(1L), "Test 1"),
            chatInstance = "",
            message = message,
            data = "TEST-42:+"
        )

        coEvery { votesDAO.get("TEST-42") } returns Votes("TEST-42", listOf("+", "-"))
        coEvery { votesDAO.save(any()) } just runs
        coEvery { bot.answerCallbackQuery(callbackQuery) } returns true

        sut.processCallbackQuery(callbackQuery)

        coVerify(exactly = 1) { votesDAO.save(Votes("TEST-42", listOf("+", "-"), mapOf("1" to "+"))) }
        coVerify(exactly = 1) { bot.answerCallbackQuery(callbackQuery) }
        coVerify(exactly = 1) {
            bot.editMessageReplyMarkup(
                message = message,
                replyMarkup = Votes("TEST-42", listOf("+", "-"), mapOf("1" to "+")).toInlineKeyboardMarkup(),
            )
        }

        clearAllMocks()
    }

    @Test
    fun processCallbackQueryForNewVotes() = runBlocking {
        val message = CommonGroupContentMessageImpl(
            chat = GroupChatImpl(
                id = ChatId(1L),
                title = "Test"
            ),
            messageId = 1L,
            user = CommonUser(UserId(2L), "Test 2"),
            date = DateTime.now(),
            forwardInfo = null,
            editDate = null,
            replyTo = null,
            replyMarkup = null,
            content = TextContent(""),
            senderBot = null,
        )
        val callbackQuery = MessageDataCallbackQuery(
            id = "",
            user = CommonUser(UserId(1L), "Test 1"),
            chatInstance = "",
            message = message,
            data = "TEST-42:+"
        )

        coEvery { votesDAO.get("TEST-42") } returns null
        every { votesConstructor.invoke("TEST-42") } returns Votes("TEST-42", listOf("+", "-", "="))
        coEvery { votesDAO.save(any()) } just runs
        coEvery { bot.answerCallbackQuery(callbackQuery) } returns true

        sut.processCallbackQuery(callbackQuery)

        coVerify(exactly = 1) { votesDAO.save(Votes("TEST-42", listOf("+", "-", "="), mapOf("1" to "+"))) }
        coVerify(exactly = 1) { bot.answerCallbackQuery(callbackQuery) }
        coVerify(exactly = 1) {
            bot.editMessageReplyMarkup(
                message = message,
                replyMarkup = Votes("TEST-42", listOf("+", "-", "="), mapOf("1" to "+")).toInlineKeyboardMarkup(),
            )
        }

        clearAllMocks()
    }

    @Test
    fun processInlineMessageIdDataCallbackQuery() = runBlocking {
        val callbackQuery = InlineMessageIdDataCallbackQuery(
            id = "",
            user = CommonUser(UserId(1L), "Test 1"),
            chatInstance = "",
            inlineMessageId = "300",
            data = "TEST-42:+"
        )

        coEvery { votesDAO.get("TEST-42") } returns null
        every { votesConstructor.invoke("TEST-42") } returns Votes("TEST-42", listOf("+", "-"))
        coEvery { votesDAO.save(any()) } just runs
        coEvery { bot.answerCallbackQuery(callbackQuery) } returns true

        sut.processCallbackQuery(callbackQuery)

        coVerify(exactly = 1) { votesDAO.save(Votes("TEST-42", listOf("+", "-"), mapOf("1" to "+"))) }
        coVerify(exactly = 1) { bot.answerCallbackQuery(callbackQuery) }
        coVerify(exactly = 1) {
            bot.editMessageReplyMarkup(
                inlineMessageId = "300",
                replyMarkup = Votes("TEST-42", listOf("+", "-"), mapOf("1" to "+")).toInlineKeyboardMarkup(),
            )
        }

        clearAllMocks()
    }

    @Test
    fun processUnknownCallbackQuery() = runBlocking {
        sut.processCallbackQuery(
            MessageGameShortNameCallbackQuery(
                id = "test",
                user = mockk(),
                chatInstance = "TEST",
                message = mockk(),
                gameShortName = "TEST"
            )
        )

        verify { listOf(votesDAO, bot, votesConstructor) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processAlienCallbackQuery() = runBlocking {
        sut.processCallbackQuery(
            MessageDataCallbackQuery(
                id = "test",
                user = mockk(),
                chatInstance = "TEST",
                message = mockk(),
                data = "CHECK-42:+"
            )
        )

        verify { listOf(votesDAO, bot, votesConstructor) wasNot called }
    }

    @Test
    fun processBadDataCallbackQuery() = runBlocking {
        sut.processCallbackQuery(
            MessageDataCallbackQuery(
                id = "test",
                user = mockk(),
                chatInstance = "TEST",
                message = mockk(),
                data = "CHECK-42"
            )
        )

        verify { listOf(votesDAO, bot, votesConstructor) wasNot called }
    }
}
