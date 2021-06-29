package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.dialogs.dao.DialogStateDAO
import by.jprof.telegram.bot.dialogs.model.DialogState
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForOptions
import by.jprof.telegram.bot.quizoji.dao.QuizojiDAO
import by.jprof.telegram.bot.quizoji.model.Quizoji
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import com.soywiz.klock.DateTime
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.CommonUser
import dev.inmo.tgbotapi.types.MessageEntity.textsources.RegularTextSource
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.chat.abstracts.ChannelChat
import dev.inmo.tgbotapi.types.message.PrivateContentMessageImpl
import dev.inmo.tgbotapi.types.message.abstracts.ChannelContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.media.AudioContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.PollUpdate
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class QuizojiDoneCommandUpdateProcessorTest {
    @MockK(relaxed = true)
    private lateinit var bot: RequestsExecutor

    @MockK
    private lateinit var dialogStateDAO: DialogStateDAO

    @MockK
    private lateinit var quizojiDAO: QuizojiDAO

    @MockK
    private lateinit var votesDAO: VotesDAO

    lateinit var sut: QuizojiDoneCommandUpdateProcessor

    @BeforeEach
    fun setUp() {
        sut = QuizojiDoneCommandUpdateProcessor(
            dialogStateDAO = dialogStateDAO,
            quizojiDAO = quizojiDAO,
            votesDAO = votesDAO,
            bot = bot,
        )
    }

    @Test
    fun processNonMessageUpdate() = runBlocking {
        sut.process(
            PollUpdate(
                updateId = 1,
                data = mockk()
            )
        )

        verify { listOf(dialogStateDAO, quizojiDAO, votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processNonPrivateContentMessage() = runBlocking {
        sut.process(
            MessageUpdate(
                updateId = 1,
                data = mockk<ChannelContentMessage<*>>()
            )
        )

        verify { listOf(dialogStateDAO, quizojiDAO, votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processNonPrivateChat() = runBlocking {
        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = mockk(),
                    chat = mockk<ChannelChat>(),
                    content = mockk(),
                    date = DateTime.now(),
                    editDate = null,
                    forwardInfo = null,
                    replyTo = null,
                    replyMarkup = null,
                    senderBot = null,
                    paymentInfo = null,
                )
            )
        )

        verify { listOf(dialogStateDAO, quizojiDAO, votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processNonWaitingForOptions() = runBlocking {
        coEvery { dialogStateDAO.get(1, 2) }.returns(object : DialogState {
            override val chatId = 1L
            override val userId = 2L
        })

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = CommonUser(ChatId(2), "Test"),
                    chat = PrivateChatImpl(ChatId(1)),
                    content = TextContent("/done"),
                    date = DateTime.now(),
                    editDate = null,
                    forwardInfo = null,
                    replyTo = null,
                    replyMarkup = null,
                    senderBot = null,
                    paymentInfo = null,
                )
            )
        )

        coVerify(exactly = 1) { dialogStateDAO.get(1, 2) }
        verify { listOf(quizojiDAO, votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processNonTextContentMessage() = runBlocking {
        coEvery { dialogStateDAO.get(1, 2) }.returns(WaitingForOptions(1, 2, TextContent(""), listOf("")))

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = CommonUser(ChatId(2), "Test"),
                    chat = PrivateChatImpl(ChatId(1)),
                    content = mockk<AudioContent>(),
                    date = DateTime.now(),
                    editDate = null,
                    forwardInfo = null,
                    replyTo = null,
                    replyMarkup = null,
                    senderBot = null,
                    paymentInfo = null,
                )
            )
        )

        coVerify(exactly = 1) { dialogStateDAO.get(1, 2) }
        verify { listOf(quizojiDAO, votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processWrongCommand() = runBlocking {
        coEvery { dialogStateDAO.get(1, 2) }.returns(WaitingForOptions(1, 2, TextContent(""), listOf("")))

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = CommonUser(ChatId(2), "Test"),
                    chat = PrivateChatImpl(ChatId(1)),
                    content = TextContent("/start doing your morning exercise"),
                    date = DateTime.now(),
                    editDate = null,
                    forwardInfo = null,
                    replyTo = null,
                    replyMarkup = null,
                    senderBot = null,
                    paymentInfo = null,
                )
            )
        )

        coVerify(exactly = 1) { dialogStateDAO.get(1, 2) }
        verify { listOf(quizojiDAO, votesDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processTextQuestion() = runBlocking {
        val question = TextContent("Question", listOf(RegularTextSource("Question")))

        coEvery { dialogStateDAO.get(1, 2) }.returns(WaitingForOptions(1, 2, question, listOf("1", "2")))
        coEvery { votesDAO.save(any()) } just runs
        coEvery { quizojiDAO.save(any()) } just runs
        coEvery { dialogStateDAO.delete(any(), any()) } just runs

        val chat = PrivateChatImpl(ChatId(1))

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = CommonUser(ChatId(2), "Test"),
                    chat = chat,
                    content = TextContent("/done"),
                    date = DateTime.now(),
                    editDate = null,
                    forwardInfo = null,
                    replyTo = null,
                    replyMarkup = null,
                    senderBot = null,
                    paymentInfo = null,
                )
            )
        )
        val quizojiSlot = CapturingSlot<Quizoji>()
        val votesSlot = CapturingSlot<Votes>()

        coVerify(exactly = 1) { dialogStateDAO.get(1, 2) }
        coVerify(exactly = 1) { quizojiDAO.save(capture(quizojiSlot)) }
        Assertions.assertEquals(question, quizojiSlot.captured.question)
        coVerify(exactly = 1) { votesDAO.save(capture(votesSlot)) }
        Assertions.assertEquals("QUIZOJI-${quizojiSlot.captured.id}", votesSlot.captured.id)
        Assertions.assertEquals(listOf("1", "2"), votesSlot.captured.options)
        coVerify(exactly = 1) { dialogStateDAO.delete(1, 2) }
        coVerify(exactly = 1) {
            bot.sendMessage(
                chat = chat,
                text = "Quizoji created! Use the 'Publish' button to send it."
            )
        }
        coVerify {
            bot.sendMessage(
                chat = chat,
                entities = question.textSources,
                replyMarkup = InlineKeyboardMarkup(
                    listOf(
                        listOf(
                            CallbackDataInlineKeyboardButton(
                                text = "0 1",
                                callbackData = "QUIZOJI-${quizojiSlot.captured.id}:1"
                            ),
                            CallbackDataInlineKeyboardButton(
                                text = "0 2",
                                callbackData = "QUIZOJI-${quizojiSlot.captured.id}:2"
                            )
                        ),
                        listOf(
                            SwitchInlineQueryInlineKeyboardButton(
                                text = "Publish",
                                switchInlineQuery = "quizoji ${quizojiSlot.captured.id}",
                            )
                        ),
                    )
                )
            )
        }

        clearAllMocks()
    }
}
