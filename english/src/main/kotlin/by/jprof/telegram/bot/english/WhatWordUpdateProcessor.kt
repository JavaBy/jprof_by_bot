package by.jprof.telegram.bot.english

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.english.language_rooms.dao.LanguageRoomDAO
import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.language_rooms.model.Violence
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.media.sendAnimation
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.requests.abstracts.MultipartFile
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.ktor.utils.io.streams.asInput
import org.apache.logging.log4j.LogManager

@OptIn(PreviewFeature::class)
class WhatWordUpdateProcessor(
    private val languageRoomDAO: LanguageRoomDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(WhatWordUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        @Suppress("NAME_SHADOWING") val update = update.asBaseMessageUpdate() ?: return
        val roomId = update.data.chat.id
        val message = update.data.asContentMessage() ?: return
        val content = message.content.asTextContent() ?: return

        if (
            languageRoomDAO.get(roomId.chatId, roomId.threadId)?.takeIf { it.language == Language.ENGLISH && it.violence == Violence.MOTHERFUCKER } == null
        ) {
            return
        }

        if (content.text.contains(Regex("w((ha)|(a)|(o)|(u))t\\?", RegexOption.IGNORE_CASE))) {
            bot.sendAnimation(
                chat = message.chat,
                animation = MultipartFile(
                    filename = "say what again.gif",
                    inputSource = {
                        this::class.java.getResourceAsStream("/say what again.gif").asInput()
                    },
                ),
                replyToMessageId = message.messageId,
            )
        }
    }
}
