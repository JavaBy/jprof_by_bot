package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.dialogs.dao.DialogStateDAO
import by.jprof.telegram.bot.dialogs.model.DialogState
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForOptions
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForQuestion
import com.soywiz.klock.DateTime
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.CommonUser
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.chat.abstracts.ChannelChat
import dev.inmo.tgbotapi.types.dice.Dice
import dev.inmo.tgbotapi.types.dice.SlotMachineDiceAnimationType
import dev.inmo.tgbotapi.types.message.PrivateContentMessageImpl
import dev.inmo.tgbotapi.types.message.abstracts.ChannelContentMessage
import dev.inmo.tgbotapi.types.message.content.DiceContent
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.PollUpdate
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

@ExtendWith(MockKExtension::class)
internal class QuizojiQuestionUpdateProcessorTest {
    @MockK(relaxed = true)
    private lateinit var bot: RequestsExecutor

    @MockK(relaxed = true)
    private lateinit var dialogStateDAO: DialogStateDAO

    lateinit var sut: QuizojiQuestionUpdateProcessor

    @BeforeEach
    fun setUp() {
        sut = QuizojiQuestionUpdateProcessor(
            dialogStateDAO = dialogStateDAO,
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

        verify { listOf(dialogStateDAO, bot) wasNot called }

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

        verify { listOf(dialogStateDAO, bot) wasNot called }

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

        verify { listOf(dialogStateDAO, bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processNonWaitingForQuestion() = runBlocking {
        val chat = PrivateChatImpl(
            id = ChatId(1),
        )

        coEvery { dialogStateDAO.get(1, 2) }.returns(object : DialogState {
            override val chatId = 1L
            override val userId = 2L
        })

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = CommonUser(id = ChatId(2), "Test"),
                    chat = chat,
                    content = TextContent(
                        text = "Test"
                    ),
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
        verify { listOf(bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processUnsupportedContent() = runBlocking {
        val chat = PrivateChatImpl(
            id = ChatId(1),
        )

        coEvery { dialogStateDAO.get(1, 2) }.returns(WaitingForQuestion(1, 2))

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = CommonUser(id = ChatId(2), "Test"),
                    chat = chat,
                    content = DiceContent(dice = Dice(value = 3, animationType = SlotMachineDiceAnimationType)),
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
        coVerify(exactly = 1) {
            bot.sendMessage(
                chat = chat,
                text = "Unsupported question type: Dice"
            )
        }

        clearAllMocks()
    }

    @Test
    fun process() = runBlocking {
        val chat = PrivateChatImpl(
            id = ChatId(1),
        )
        val content = TextContent(
            text = "Test"
        )

        coEvery { dialogStateDAO.get(1, 2) }.returns(WaitingForQuestion(1, 2))

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = CommonUser(id = ChatId(2), "Test"),
                    chat = chat,
                    content = content,
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
        coVerify(exactly = 1) {
            dialogStateDAO.save(
                WaitingForOptions(
                    chatId = 1,
                    userId = 2,
                    question = content
                )
            )
        }
        coVerify(exactly = 1) {
            bot.sendMessage(
                chat = chat,
                text = "Now send me the options, one per message\\. When done, send /done\\.\n\n_Up to 8 options are recommended, otherwise the buttons will be wrapped in multiple lines\\._",
                parseMode = MarkdownV2
            )
        }

        clearAllMocks()
    }
}
