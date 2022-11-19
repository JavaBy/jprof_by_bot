package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.quizoji.dao.QuizojiDAO
import by.jprof.telegram.bot.quizoji.model.Quizoji
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerInlineQuery
import dev.inmo.tgbotapi.types.InlineQueries.query.BaseInlineQuery
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.RegularTextSource
import dev.inmo.tgbotapi.types.update.InlineQueryUpdate
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@RiskFeature
@PreviewFeature
@ExtendWith(MockKExtension::class)
internal class QuizojiInlineQueryUpdateProcessorTest {
    @MockK
    private lateinit var quizojiDAO: QuizojiDAO

    @MockK
    private lateinit var votesDAO: VotesDAO

    @MockK(relaxed = true)
    private lateinit var bot: RequestsExecutor

    lateinit var sut: QuizojiInlineQueryUpdateProcessor

    @BeforeEach
    fun setUp() {
        sut = QuizojiInlineQueryUpdateProcessor(
            quizojiDAO = quizojiDAO,
            votesDAO = votesDAO,
            bot = bot,
        )
    }

    @Test
    fun processNonInilineQuery() = runBlocking {
        sut.process(
            MessageUpdate(
                updateId = 1L,
                data = mockk()
            )
        )

        verify { listOf(quizojiDAO, votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processAlienInilineQuery() = runBlocking {
        sut.process(
            InlineQueryUpdate(
                updateId = 1L,
                data = BaseInlineQuery(
                    id = "QuizojiStartCommandUpdateProcessorTest",
                    from = mockk(),
                    query = "alien",
                    offset = "",
                    chatType = null,
                )
            )
        )

        verify { listOf(quizojiDAO, votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processNewQuizojiInilineQuery() = runBlocking {
        val inlineQuery = BaseInlineQuery(
            id = "QuizojiStartCommandUpdateProcessorTest",
            from = mockk(),
            query = "quizoji",
            offset = "",
            chatType = null,
        )

        sut.process(
            InlineQueryUpdate(
                updateId = 1L,
                data = inlineQuery
            )
        )

        coVerify(exactly = 1) {
            bot.answerInlineQuery(
                inlineQuery = inlineQuery,
                switchPmText = "Create new quizoji",
                switchPmParameter = "quizoji",
            )
        }
        verify { listOf(quizojiDAO, votesDAO) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processUnknownQuizojiIDInilineQuery() = runBlocking {
        val inlineQuery = BaseInlineQuery(
            id = "QuizojiStartCommandUpdateProcessorTest",
            from = mockk(),
            query = "quizoji id",
            offset = "",
            chatType = null,
        )

        coEvery { quizojiDAO.get("id") }.returns(null)

        sut.process(
            InlineQueryUpdate(
                updateId = 1L,
                data = inlineQuery
            )
        )

        coVerify(exactly = 1) {
            quizojiDAO.get("id")
        }

        verify { listOf(votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processUnexistingVotesQuizojiIDInilineQuery() = runBlocking {
        val inlineQuery = BaseInlineQuery(
            id = "QuizojiStartCommandUpdateProcessorTest",
            from = mockk(),
            query = "quizoji id",
            offset = "",
            chatType = null,
        )

        coEvery { quizojiDAO.get("id") }.returns(Quizoji("id", TextContent("Question")))
        coEvery { votesDAO.get("QUIZOJI-id") }.returns(null)

        sut.process(
            InlineQueryUpdate(
                updateId = 1L,
                data = inlineQuery
            )
        )

        coVerify(exactly = 1) {
            quizojiDAO.get("id")
        }
        coVerify(exactly = 1) {
            votesDAO.get("QUIZOJI-id")
        }

        verify { listOf(bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processQuizojiIDInilineQuery() = runBlocking {
        val inlineQuery = BaseInlineQuery(
            id = "QuizojiStartCommandUpdateProcessorTest",
            from = mockk(),
            query = "quizoji id",
            offset = "",
            chatType = null,
        )

        coEvery { quizojiDAO.get("id") }.returns(
            Quizoji(
                "id",
                TextContent("Question", listOf(RegularTextSource("Question")))
            )
        )
        coEvery { votesDAO.get("QUIZOJI-id") }.returns(Votes("QUIZOJI-id", listOf("1", "2"), mapOf("1" to "1")))

        sut.process(
            InlineQueryUpdate(
                updateId = 1L,
                data = inlineQuery
            )
        )

        coVerify(exactly = 1) {
            quizojiDAO.get("id")
        }
        coVerify(exactly = 1) {
            votesDAO.get("QUIZOJI-id")
        }
        //        coVerify(exactly = 1) {
        //            bot.answerInlineQuery(
        //                inlineQuery = inlineQuery,
        //                results = any()
        //            )
        //        }

        clearAllMocks()
    }
}
