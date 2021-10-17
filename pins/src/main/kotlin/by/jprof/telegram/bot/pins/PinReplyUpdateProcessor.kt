package by.jprof.telegram.bot.pins

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.monies.dao.MoniesDAO
import by.jprof.telegram.bot.monies.model.Money
import by.jprof.telegram.bot.monies.model.Monies
import by.jprof.telegram.bot.pins.dao.PinDAO
import dev.inmo.tgbotapi.types.message.abstracts.FromUserMessage
import dev.inmo.tgbotapi.types.message.abstracts.PossiblyReplyMessage
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class PinReplyUpdateProcessor(
    private val moniesDAO: MoniesDAO,
    private val pinDAO: PinDAO,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(PinReplyUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val replyTo = ((update as? MessageUpdate)?.data as? PossiblyReplyMessage)?.replyTo ?: return
        val replier = ((update as? MessageUpdate)?.data as? FromUserMessage)?.user ?: return
        val pin = pinDAO.get(replyTo.chat.id.chatId, replyTo.messageId) ?: return

        if (replier.id.chatId == pin.userId) {
            return
        }

        logger.info("{} replied to {}", replier, pin)

        val monies = moniesDAO.get(pin.userId, replyTo.chat.id.chatId) ?: Monies(pin.userId, replyTo.chat.id.chatId)

        moniesDAO.save(
            monies.copy(monies = monies.monies + (Money.PINS to (monies.monies[Money.PINS] ?: 0) + 1))
        )
    }
}
