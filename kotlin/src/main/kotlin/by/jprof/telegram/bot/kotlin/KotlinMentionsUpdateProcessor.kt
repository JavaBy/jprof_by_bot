package by.jprof.telegram.bot.kotlin

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.kotlin.dao.KotlinMentionsDAO
import by.jprof.telegram.bot.kotlin.model.KotlinMentions
import by.jprof.telegram.bot.kotlin.model.UserStatistics
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.utils.asCommonMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.requests.abstracts.MultipartFile
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.abstracts.FromUserMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.ktor.utils.io.streams.asInput
import java.io.InputStream
import java.time.Duration
import java.time.Instant
import org.apache.logging.log4j.LogManager
import org.jetbrains.skija.Data
import org.jetbrains.skija.EncodedImageFormat
import org.jetbrains.skija.Font
import org.jetbrains.skija.Image
import org.jetbrains.skija.Paint
import org.jetbrains.skija.Surface
import org.jetbrains.skija.TextLine
import org.jetbrains.skija.Typeface

@PreviewFeature
class KotlinMentionsUpdateProcessor(
    private val kotlinMentionsDAO: KotlinMentionsDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(KotlinMentionsUpdateProcessor::class.java)!!
        private val kotlinRegex =
            "([kкκ]+[._\\-/#*\\\\+=]{0,2}[оo0aа]+[._\\-/#*\\\\+=]{0,2}[тtτ]+[._\\-/#*\\\\+=]{0,2}[лlλ]+[._\\-/#*\\\\+=]{0,2}[ие1ieіι]+[._\\-/#*\\\\+=]{0,2}[нnHνη]+)".toRegex(
                setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
            )
    }

    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asCommonMessage() ?: return

        when (val content = message.content) {
            is TextContent -> if (!kotlinRegex.containsMatchIn(content.text)) return
            else -> return
        }

        logger.debug("Kotlin mentioned!")

        val now = Instant.now()
        val user = (message as? FromUserMessage)?.user ?: return
        val chat = message.chat
        val mentions = kotlinMentionsDAO.get(chat.id.chatId) ?: KotlinMentions(
            chat.id.chatId,
            now
        )
        val timeSinceLastMention = Duration.between(mentions.lastMention, now)

        if (timeSinceLastMention.toMinutes() >= 15) {
            incident(message, chat, timeSinceLastMention)
        }

        val oldUserStatistics = mentions.usersStatistics[user.id.chatId] ?: UserStatistics(0, now)
        val newUserStatistics = oldUserStatistics.copy(count = oldUserStatistics.count + 1, lastMention = now)

        kotlinMentionsDAO.save(
            mentions.copy(
                lastMention = now,
                usersStatistics = mentions.usersStatistics + (user.id.chatId to newUserStatistics)
            )
        )
    }

    private suspend fun incident(
        message: CommonMessage<*>,
        chat: Chat,
        timeSinceLastMention: Duration,
    ) {
        val templateImage = Image.makeFromEncoded(
            this::class.java.getResourceAsStream("/template.png")!!
                .use(InputStream::readBytes)
        )
        val surface = Surface.makeRasterN32Premul(templateImage.width, templateImage.height)
        val canvas = surface.canvas

        canvas.drawImage(templateImage, 0F, 0F)

        val typeface = Typeface.makeFromData(
            Data.makeFromBytes(
                this::class.java.getResourceAsStream("/ComingSoon-Regular.ttf")!!
                    .use(InputStream::readBytes)
            )
        )
        val font = Font(typeface, 32F)
        val line = TextLine.make(
            "%02dd:%02dh:%02dm:%02ds".format(
                timeSinceLastMention.toDaysPart(),
                timeSinceLastMention.toHoursPart(),
                timeSinceLastMention.toMinutesPart(),
                timeSinceLastMention.toSecondsPart()
            ),
            font
        )
        val paint = Paint().apply {
            color = 0xFFB8B19E.toInt()
        }
        canvas.drawTextLine(
            line,
            700F,
            260F,
            paint
        )

        val snapshot = surface.makeImageSnapshot() ?: return
        val data = snapshot.encodeToData(EncodedImageFormat.JPEG, 95) ?: return

        bot.sendPhoto(
            chat = chat,
            fileId = MultipartFile(
                filename = "darryl.jpeg",
                inputSource = { data.bytes.inputStream().asInput() }
            ),
            replyToMessageId = message.messageId,
        )

        logger.debug("Kotlin mention reported!")
    }
}
