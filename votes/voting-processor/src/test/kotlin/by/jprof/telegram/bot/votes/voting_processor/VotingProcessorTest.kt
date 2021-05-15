package by.jprof.telegram.bot.votes.voting_processor

import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.ReplyMarkup.editMessageReplyMarkup
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.CallbackQuery.MessageGameShortNameCallbackQuery
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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
    @Disabled
    fun processCallbackQuery() = runBlocking {
        val callbackQuery = mockk<MessageDataCallbackQuery> {
            every { id } returns ""
            every { user } returns mockk {
                every { id } returns ChatId(42L)
            }
            every { chatInstance } returns ""
            every { message.hint(ContentMessage::class) } returns mockk<ContentMessage<*>> {
                every { messageId } returns 1L
                every { content.hint(TextContent::class) } returns TextContent("")
                every { chat } returns mockk {
                    every { id } returns ChatId(1L)
                }
            }
            every { data } returns "TEST-42:+"
        }

        coEvery { votesDAO.get("TEST-42") } returns Votes("TEST-42", listOf("+", "-"))
        coEvery { votesDAO.save(any()) } just runs
        coEvery { bot.answerCallbackQuery(callbackQuery) } returns true

        sut.processCallbackQuery(callbackQuery)

        coVerify(exactly = 1) { votesDAO.save(Votes("TEST-42", listOf("+", "-"), mapOf("42" to "+"))) }
        coVerify(exactly = 1) { bot.answerCallbackQuery(callbackQuery) }
        coVerify(exactly = 1) {
            bot.editMessageReplyMarkup(
                message = callbackQuery.message,
                replyMarkup = Votes("TEST-42", listOf("+", "-"), mapOf("42" to "+")).toInlineKeyboardMarkup()
            )
        }

        clearAllMocks()
    }

    @Test
    @Disabled
    fun processCallbackQueryForNewVotes() {
        TODO()
    }

    @Test
    fun processNonMessageDataCallbackQuery() = runBlocking {
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
