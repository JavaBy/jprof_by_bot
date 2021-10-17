package by.jprof.telegram.bot.pins

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.monies.dao.MoniesDAO
import by.jprof.telegram.bot.monies.model.Money
import by.jprof.telegram.bot.monies.model.Monies
import by.jprof.telegram.bot.pins.dao.PinDAO
import by.jprof.telegram.bot.pins.dto.Unpin
import by.jprof.telegram.bot.pins.model.Pin
import by.jprof.telegram.bot.pins.model.PinDuration
import by.jprof.telegram.bot.pins.scheduler.UnpinScheduler
import by.jprof.telegram.bot.pins.utils.PinRequestFinder
import by.jprof.telegram.bot.pins.utils.beggar
import by.jprof.telegram.bot.pins.utils.help
import by.jprof.telegram.bot.pins.utils.negativeDuration
import by.jprof.telegram.bot.pins.utils.tooManyPinnedMessages
import by.jprof.telegram.bot.pins.utils.tooPositiveDuration
import by.jprof.telegram.bot.pins.utils.unrecognizedDuration
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.modify.pinChatMessage
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import org.apache.logging.log4j.LogManager
import java.time.Duration

@PreviewFeature
class PinCommandUpdateProcessor(
    private val moniesDAO: MoniesDAO,
    private val pinDAO: PinDAO,
    private val unpinScheduler: UnpinScheduler,
    private val bot: RequestsExecutor,
    private val pinRequestFinder: PinRequestFinder = PinRequestFinder.DEFAULT
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(PinCommandUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        pinRequestFinder(update)?.let { pin ->
            logger.info("Pin requested: {}", pin)

            val monies = moniesDAO.get(pin.user.id.chatId, pin.chat.id.chatId) ?: Monies(pin.user.id.chatId, pin.chat.id.chatId)
            val pins = monies.pins ?: 0
            val duration = pin.duration

            if (pin.message == null) {
                bot.reply(to = pin.request, text = help(pins), parseMode = MarkdownV2)

                return
            }

            if (duration !is PinDuration.Recognized) {
                bot.reply(to = pin.request, text = unrecognizedDuration(), parseMode = MarkdownV2)

                return
            }

            if (duration.duration.isNegative || duration.duration.isZero) {
                bot.reply(to = pin.request, text = negativeDuration(), parseMode = MarkdownV2)

                return
            }

            if (duration.duration > Duration.ofDays(90)) {
                bot.reply(to = pin.request, text = tooPositiveDuration(), parseMode = MarkdownV2)

                return
            }

            if (pins < pin.price) {
                bot.reply(to = pin.request, text = beggar(pins, pin.price), parseMode = MarkdownV2)

                return
            }

            if (pinDAO.findByChatId(pin.chat.id.chatId).size >= 5) {
                bot.reply(to = pin.request, text = tooManyPinnedMessages(), parseMode = MarkdownV2)

                return
            }

            try {
                bot.pinChatMessage(pin.message, disableNotification = true)
                pinDAO.save(Pin(pin.chat.id.chatId, pin.message.messageId, pin.user.id.chatId))
                moniesDAO.save(monies.copy(monies = monies.monies + (Money.PINS to (pins - pin.price))))
                unpinScheduler.scheduleUnpin(Unpin().apply {
                    messageId = pin.message.messageId
                    chatId = pin.chat.id.chatId
                    userId = pin.user.id.chatId
                    ttl = duration.duration.seconds
                })
            } catch (e: Exception) {
                logger.error("Failed to pin a message", e)
            }
        }
    }
}
