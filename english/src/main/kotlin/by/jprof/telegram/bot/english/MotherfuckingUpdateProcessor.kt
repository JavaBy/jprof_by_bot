package by.jprof.telegram.bot.english

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.english.language_rooms.dao.LanguageRoomDAO
import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.language_rooms.model.Violence
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.send.media.sendAnimation
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.requests.abstracts.MultipartFile
import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.ktor.utils.io.streams.asInput
import kotlin.random.Random
import org.apache.logging.log4j.LogManager

class MotherfuckingUpdateProcessor(
    private val languageRoomDAO: LanguageRoomDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(MotherfuckingUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val update = update.asBaseMessageUpdate() ?: return
        val roomId = update.data.chat.id
        val message = update.data.asContentMessage() ?: return
        val content = message.content.asTextContent() ?: return

        if (
            languageRoomDAO.get(roomId.chatId, roomId.threadId)?.takeIf { it.language == Language.ENGLISH && it.violence == Violence.MOTHERFUCKER } == null
        ) {
            return
        }

        val latinLetters = content.text.count { it in 'a'..'z' || it in 'A'..'Z' }

        logger.debug("Latin letters detected: {}, total letters: {}", latinLetters, content.text.length)

        if (latinLetters.toDouble() / content.text.length < 2.toDouble() / 3) {
            when (val i = Random.nextInt(4)) {
                0, 1 -> bot.sendPhoto(
                    chat = message.chat,
                    fileId = MultipartFile(
                        filename = "english$i.png",
                        inputSource = {
                            this::class.java.getResourceAsStream("/english$i.png").asInput()
                        },
                    ),
                    replyToMessageId = message.messageId,
                )

                2, 3 -> bot.sendAnimation(
                    chat = message.chat,
                    animation = MultipartFile(
                        filename = "english$i.gif",
                        inputSource = {
                            this::class.java.getResourceAsStream("/english$i.gif").asInput()
                        },
                    ),
                    replyToMessageId = message.messageId,
                )
            }
            bot.delete(message)
        }
    }
}