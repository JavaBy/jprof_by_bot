package by.jprof.telegram.bot.english

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.english.language_rooms.dao.LanguageRoomDAO
import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.language_rooms.model.LanguageRoom
import by.jprof.telegram.bot.english.language_rooms.model.Violence
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.extensions.api.send.media.sendAnimation
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asBotCommandTextSource
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.requests.abstracts.MultipartFile
import dev.inmo.tgbotapi.types.chat.member.AdministratorChatMember
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import io.ktor.utils.io.streams.asInput
import org.apache.logging.log4j.LogManager

@OptIn(PreviewFeature::class, RiskFeature::class)
class EnglishCommandUpdateProcessor(
    private val languageRoomDAO: LanguageRoomDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(EnglishCommandUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        @Suppress("NAME_SHADOWING") val update = update.asBaseMessageUpdate() ?: return
        val message = update.data.asContentMessage() ?: return
        val content = message.content.asTextContent() ?: return
        val (_, argument) = (content.textSources + null)
            .zipWithNext()
            .firstOrNull { it.first?.asBotCommandTextSource()?.command == "english" } ?: return
        val roomId = update.data.chat.id
        val languageRoom = languageRoomDAO.get(roomId.chatId, roomId.threadId)
        val englishRoom = LanguageRoom(roomId.chatId, roomId.threadId, Language.ENGLISH, Violence.POLITE, true)

        if (argument == null) {
            bot.reply(
                to = message,
                text = if ((languageRoom == null) || (languageRoom.language != Language.ENGLISH)) {
                    "This is not an English Room\\!"
                } else {
                    "This is ${if (languageRoom.violence == Violence.MOTHERFUCKER) "a motherfucking \uD83E\uDD2C" else "an"} English Room\\!"
                },
                parseMode = MarkdownV2ParseMode,
            )
        } else {
            val member = bot.getChatMember(message.chat.id, message.from ?: return)

            if (member is AdministratorChatMember) {
                if (argument.source.trim() == "on") {
                    val languageRoomConfig = (languageRoom ?: englishRoom).copy(language = Language.ENGLISH, violence = Violence.POLITE)

                    languageRoomDAO.save(languageRoomConfig)
                    bot.reply(to = message, text = "This room is set to be an English Room\\!", parseMode = MarkdownV2ParseMode)
                } else if ((argument.source.trim() == "motherfucker") || (argument.source.trim() == "motherfuckers")) {
                    val languageRoomConfig = (languageRoom
                        ?: englishRoom).copy(language = Language.ENGLISH, violence = Violence.MOTHERFUCKER)

                    languageRoomDAO.save(languageRoomConfig)
                    bot.reply(to = message, text = "This room is set to be a motherfucking \uD83E\uDD2C English Room\\!", parseMode = MarkdownV2ParseMode)
                } else if ((argument.source.trim() == "off") && (languageRoom?.language == Language.ENGLISH)) {
                    languageRoomDAO.delete(roomId.chatId, roomId.threadId)
                    bot.reply(to = message, text = "Ok, this is not an English Room anymore\\!", parseMode = MarkdownV2ParseMode)
                }
            } else {
                bot.sendAnimation(
                    chat = message.chat,
                    animation = MultipartFile(
                        filename = "no power.gif",
                        inputSource = {
                            this::class.java.getResourceAsStream("/no power.gif").asInput()
                        },
                    ),
                    replyToMessageId = message.messageId,
                )
            }
        }
    }
}
