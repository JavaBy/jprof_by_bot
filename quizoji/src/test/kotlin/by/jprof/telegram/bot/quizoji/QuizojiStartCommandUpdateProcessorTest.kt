package by.jprof.telegram.bot.quizoji

import com.soywiz.klock.DateTime
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.Bot
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.chat.abstracts.PrivateChat
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class QuizojiStartCommandUpdateProcessorTest {
    @MockK(relaxed = true)
    private lateinit var bot: RequestsExecutor

    lateinit var sut: QuizojiStartCommandUpdateProcessor

    @BeforeEach
    fun setUp() {
        sut = QuizojiStartCommandUpdateProcessor(
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

        verify { listOf(bot) wasNot called }

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

        verify { listOf(bot) wasNot called }

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
                    chat = mockk<Bot>(),
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

        verify { listOf(bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processNonTextContentMessage() = runBlocking {
        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = mockk(),
                    chat = mockk<PrivateChat>(),
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

        verify { listOf(bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun processWrongCommand() = runBlocking {
        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = mockk(),
                    chat = mockk<PrivateChat>(),
                    content = TextContent(
                        text = "/start doing your morning exercise"
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

        verify { listOf(bot) wasNot called }

        clearAllMocks()
    }

    @Test
    fun process() = runBlocking {
        val chat = PrivateChatImpl(
            id = ChatId(1),
        )

        sut.process(
            MessageUpdate(
                updateId = 1,
                data = PrivateContentMessageImpl(
                    messageId = 1,
                    user = mockk(),
                    chat = chat,
                    content = TextContent(
                        text = "/start quizoji"
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

        coVerify(exactly = 1) {
            bot.sendMessage(
                chat = chat,
                text = "Let's create a Quizoji! First, send me the message. It can be anything — a text, photo, video, even a sticker."
            )
        }

        clearAllMocks()
    }
}
