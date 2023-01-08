package by.jprof.telegram.bot.english.urban_dictionary_daily

import by.jprof.telegram.bot.english.language_rooms.dao.LanguageRoomDAO
import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.urban_dictionary.UrbanDictionaryClient
import by.jprof.telegram.bot.english.urban_dictionary_daily.config.databaseModule
import by.jprof.telegram.bot.english.urban_dictionary_daily.config.envModule
import by.jprof.telegram.bot.english.urban_dictionary_daily.config.jsonModule
import by.jprof.telegram.bot.english.urban_dictionary_daily.config.sfnModule
import by.jprof.telegram.bot.english.urban_dictionary_daily.config.telegramModule
import by.jprof.telegram.bot.english.urban_dictionary_daily.config.urbanDictionaryModule
import by.jprof.telegram.bot.english.urban_dictionary_daily.model.Event
import by.jprof.telegram.bot.english.urban_dictionary_definition_formatter.format
import by.jprof.telegram.bot.english.urban_word_of_the_day.dao.UrbanWordOfTheDayDAO
import by.jprof.telegram.bot.english.urban_word_of_the_day.model.UrbanWordOfTheDay
import by.jprof.telegram.bot.pins.dto.Unpin
import by.jprof.telegram.bot.pins.scheduler.UnpinScheduler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.modify.pinChatMessage
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.ChatIdWithThreadId
import dev.inmo.tgbotapi.types.message.MarkdownV2
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.logging.log4j.LogManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

@ExperimentalSerializationApi
@Suppress("unused")
class Handler : RequestStreamHandler, KoinComponent {
    companion object {
        private val logger = LogManager.getLogger(Handler::class.java)
    }

    init {
        startKoin {
            modules(
                envModule,
                jsonModule,
                urbanDictionaryModule,
                databaseModule,
                telegramModule,
                sfnModule,
            )
        }
    }

    private val json: Json by inject()
    private val urbanDictionaryClient: UrbanDictionaryClient by inject()
    private val urbanWordOfTheDayDAO: UrbanWordOfTheDayDAO by inject()
    private val languageRoomDAO: LanguageRoomDAO by inject()
    private val bot: RequestsExecutor by inject()
    private val unpinScheduler: UnpinScheduler by inject()

    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) = runBlocking {
        val event = json.decodeFromStream<Event>(input)

        logger.debug("Parsed event: {}", event)

        val term = event.records.firstOrNull()?.ses?.mail?.subject ?: return@runBlocking
        val definition = urbanDictionaryClient.define(term).maxByOrNull { it.thumbsUp } ?: return@runBlocking

        logger.debug("Definition: {}", definition)

        val urbanWordOfTheDay = UrbanWordOfTheDay(
            date = LocalDate.now(),
            word = definition.word,
            definition = definition.definition,
            example = definition.example,
            permalink = definition.permalink,
        )

        urbanWordOfTheDayDAO.save(urbanWordOfTheDay)
        languageRoomDAO.getAll().filter { it.language == Language.ENGLISH && it.urbanWordOfTheDay }.forEach { languageRoom ->
            val message = bot.sendMessage(
                chatId = languageRoom.threadId?.let { ChatIdWithThreadId(languageRoom.chatId, it) } ?: ChatId(languageRoom.chatId),
                text = urbanWordOfTheDay.format().also { logger.debug("Formatted message: {}", it) },
                parseMode = MarkdownV2,
                disableWebPagePreview = true,
            )

            bot.pinChatMessage(message, disableNotification = true)
            unpinScheduler.scheduleUnpin(
                Unpin().apply {
                    chatId = languageRoom.chatId
                    messageId = message.messageId
                    ttl = (24 * 1.5 * 60 * 60).toLong()
                }
            )
        }
    }
}
