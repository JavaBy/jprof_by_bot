package by.jprof.telegram.bot.quizoji

import by.jprof.telegram.bot.dialogs.dao.DialogStateDAO
import by.jprof.telegram.bot.dialogs.model.DialogState
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForOptions
import com.soywiz.klock.DateTime
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.CommonUser
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
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
internal class QuizojiOptionUpdateProcessorTest {
    @MockK(relaxed = true)
    private lateinit var bot: RequestsExecutor

    @MockK(relaxed = true)
    private lateinit var dialogStateDAO: DialogStateDAO

    lateinit var sut: QuizojiOptionUpdateProcessor

    @BeforeEach
    fun setUp() {
        sut = QuizojiOptionUpdateProcessor(
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
    fun processNonWaitingForOptions() = runBlocking {
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

        coEvery { dialogStateDAO.get(1, 2) }.returns(WaitingForOptions(1, 2, TextContent("Test")))

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
                text = "Unsupported option type: Dice"
            )
        }

        clearAllMocks()
    }

    @Test
    fun processDoneCommand() = runBlocking {
        val chat = PrivateChatImpl(
            id = ChatId(1),
        )

        coEvery { dialogStateDAO.get(1, 2) }.returns(WaitingForOptions(1, 2, TextContent("Test")))

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = CommonUser(id = ChatId(2), "Test"),
                    chat = chat,
                    content = TextContent(
                        text = "/done",
                        textSources = listOf(BotCommandTextSource("done"))
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
    fun process() = runBlocking {
        val chat = PrivateChatImpl(
            id = ChatId(1),
        )
        val content = TextContent(
            text = "Option"
        )

        coEvery { dialogStateDAO.get(1, 2) }.returns(WaitingForOptions(1, 2, TextContent("Test")))

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
                    question = TextContent("Test"),
                    options = listOf("Option")
                )
            )
        }
        coVerify(exactly = 1) {
            bot.sendMessage(
                chat = chat,
                text = "Send more options or /done when ready\\.\n\n_Up to 7 more options are recommended\\._",
                parseMode = MarkdownV2
            )
        }

        clearAllMocks()
    }

    @Test
    fun processManyOptions() = runBlocking {
        val chat = PrivateChatImpl(
            id = ChatId(1),
        )
        val content = TextContent(
            text = "Option 8"
        )

        coEvery { dialogStateDAO.get(1, 2) }.returns(
            WaitingForOptions(
                1,
                2,
                TextContent("Test"),
                listOf(
                    "Option 1",
                    "Option 2",
                    "Option 3",
                    "Option 4",
                    "Option 5",
                    "Option 6",
                    "Option 7",
                )
            )
        )

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
                    question = TextContent("Test"),
                    options = listOf(
                        "Option 1",
                        "Option 2",
                        "Option 3",
                        "Option 4",
                        "Option 5",
                        "Option 6",
                        "Option 7",
                        "Option 8",
                    )
                )
            )
        }
        coVerify(exactly = 1) {
            bot.sendMessage(
                chat = chat,
                text = "Send more options or /done when ready\\.",
                parseMode = MarkdownV2
            )
        }

        clearAllMocks()
    }
}
