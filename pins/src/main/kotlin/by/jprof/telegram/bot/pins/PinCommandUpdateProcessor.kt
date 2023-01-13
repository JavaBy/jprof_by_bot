package by.jprof.telegram.bot.pins

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.monies.dao.MoniesDAO
import by.jprof.telegram.bot.monies.model.Money
import by.jprof.telegram.bot.monies.model.Monies
import by.jprof.telegram.bot.pins.dao.PinDAO
import by.jprof.telegram.bot.pins.dto.Unpin
import by.jprof.telegram.bot.pins.model.Pin
import by.jprof.telegram.bot.pins.model.PinDuration
import by.jprof.telegram.bot.pins.model.PinRequest
import by.jprof.telegram.bot.pins.scheduler.UnpinScheduler
import by.jprof.telegram.bot.pins.utils.PinRequestFinder
import by.jprof.telegram.bot.pins.utils.beggar
import by.jprof.telegram.bot.pins.utils.help
import by.jprof.telegram.bot.pins.utils.negativeDuration
import by.jprof.telegram.bot.pins.utils.tooManyPinnedMessages
import by.jprof.telegram.bot.pins.utils.tooPositiveDuration
import by.jprof.telegram.bot.pins.utils.unrecognizedDuration
import by.jprof.telegram.bot.shop.payload.PinsPayload
import by.jprof.telegram.bot.shop.provider.ChatProviderTokens
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.modify.pinChatMessage
import dev.inmo.tgbotapi.extensions.api.send.payments.sendInvoice
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.payments.LabeledPrice
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import java.time.Duration
import kotlin.random.Random
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

@PreviewFeature
class PinCommandUpdateProcessor(
    private val moniesDAO: MoniesDAO,
    private val pinDAO: PinDAO,
    private val unpinScheduler: UnpinScheduler,
    private val bot: RequestsExecutor,
    private val providerTokens: ChatProviderTokens,
    private val json: Json,
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
                pinsShop(pin)

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
                pinsShop(pin)

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
                    ttl = duration.duration.seconds
                })
                if (Random.nextInt(4) == 0) {
                    pinsShop(pin)
                }
            } catch (e: Exception) {
                logger.error("Failed to pin a message", e)
            }
        }
    }

    private suspend fun pinsShop(pin: PinRequest) {
        val chatProviderToken = providerTokens[pin.request.chat.id.chatId]

        if (chatProviderToken != null) {
            bot.sendInvoice(
                chatId = pin.request.chat.id,
                title = "168 пинов",
                description = "Неделя закрепа",
                payload = json.encodeToString(PinsPayload(
                    pins = 168,
                    chat = pin.request.chat.id.chatId,
                )),
                providerToken = chatProviderToken,
                currency = "USD",
                prices = listOf(
                    LabeledPrice("Пины × 168", 200)
                ),
                startParameter = "forwarded_payment",
                replyToMessageId = pin.request.messageId,
                allowSendingWithoutReply = true,
            )
        }
    }
}
