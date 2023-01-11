package by.jprof.telegram.bot.english

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.english.language_rooms.dao.LanguageRoomDAO
import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.urban_dictionary_definition_formatter.format
import by.jprof.telegram.bot.english.urban_word_of_the_day.dao.UrbanWordOfTheDayDAO
import by.jprof.telegram.bot.english.utils.downTo
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asBotCommandTextSource
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.update.abstracts.Update
import java.time.LocalDate
import org.apache.logging.log4j.LogManager

class UrbanWordOfTheDayUpdateProcessor(
    private val languageRoomDAO: LanguageRoomDAO,
    private val urbanWordOfTheDayDAO: UrbanWordOfTheDayDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(UrbanWordOfTheDayUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val update = update.asBaseMessageUpdate() ?: return
        val roomId = update.data.chat.id
        val message = update.data.asContentMessage() ?: return
        val content = message.content.asTextContent() ?: return

        if (
            content.textSources.none {
                it.asBotCommandTextSource()?.command?.lowercase() in listOf("urban", "ud", "urbanword", "urbanwordoftheday")
            }
        ) {
            return
        }

        if (
            languageRoomDAO.get(roomId.chatId, roomId.threadId)?.takeIf { it.language == Language.ENGLISH } == null
        ) {
            return
        }

        val urbanWordOfTheDay = (LocalDate.now() downTo LocalDate.now().minusDays(5)).asSequence().firstNotNullOfOrNull {
            urbanWordOfTheDayDAO.get(it)
        }

        if (urbanWordOfTheDay != null) {
            bot.reply(
                to = message,
                text = urbanWordOfTheDay.format(),
                parseMode = MarkdownV2,
                disableWebPagePreview = true,
            )
        } else {
            bot.reply(
                to = message,
                text = "No recent words of the day \uD83D\uDE22",
                parseMode = MarkdownV2,
                disableWebPagePreview = true,
            )
        }
    }
}
