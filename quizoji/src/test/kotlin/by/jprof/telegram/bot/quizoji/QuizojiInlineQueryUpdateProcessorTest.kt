package by.jprof.telegram.bot.quizoji

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerInlineQuery
import dev.inmo.tgbotapi.types.InlineQueries.query.BaseInlineQuery
import dev.inmo.tgbotapi.types.update.InlineQueryUpdate
import dev.inmo.tgbotapi.types.update.MessageUpdate
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class QuizojiInlineQueryUpdateProcessorTest {
    @MockK(relaxed = true)
    private lateinit var bot: RequestsExecutor

    lateinit var sut: QuizojiInlineQueryUpdateProcessor

    @BeforeEach
    fun setUp() {
        sut = QuizojiInlineQueryUpdateProcessor(
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

        verify { listOf(bot) wasNot called }

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
                )
            )
        )

        verify { listOf(bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processQuizojiInilineQuery() = runBlocking {
        val inlineQuery = BaseInlineQuery(
            id = "QuizojiStartCommandUpdateProcessorTest",
            from = mockk(),
            query = "quizoji",
            offset = "",
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

        clearAllMocks()
    }
}
