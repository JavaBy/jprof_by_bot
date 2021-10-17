package by.jprof.telegram.bot.pins.unpin

import by.jprof.telegram.bot.pins.dao.PinDAO
import by.jprof.telegram.bot.pins.dto.Unpin
import by.jprof.telegram.bot.pins.unpin.config.databaseModule
import by.jprof.telegram.bot.pins.unpin.config.envModule
import by.jprof.telegram.bot.pins.unpin.config.telegramModule
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.modify.unpinChatMessage
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

@Suppress("unused")
class Handler : RequestHandler<Unpin, Int>, KoinComponent {
    companion object {
        private val logger = LogManager.getLogger(Handler::class.java)
    }

    init {
        startKoin {
            modules(
                envModule,
                databaseModule,
                telegramModule,
            )
        }
    }

    private val bot: RequestsExecutor by inject()
    private val pinDAO: PinDAO by inject()

    override fun handleRequest(input: Unpin, context: Context): Int = runBlocking {
        logger.debug("Incoming request: {}", input)

        try {
            pinDAO.delete(input.chatId, input.messageId)
            bot.unpinChatMessage(ChatId(input.chatId!!), input.messageId)
        } catch (e: Exception) {
            logger.error("Failed to unpin the message", e)
        }

        context.remainingTimeInMillis
    }
}
