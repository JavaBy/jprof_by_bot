package by.jprof.telegram.bot.times

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.times.model.TimeZoneRequest
import by.jprof.telegram.bot.times.model.TimeZoneValue
import by.jprof.telegram.bot.times.timezones.dao.TimeZoneDAO
import by.jprof.telegram.bot.times.timezones.model.TimeZone
import by.jprof.telegram.bot.times.utils.TimeZoneParser
import by.jprof.telegram.bot.times.utils.timeZoneSet
import by.jprof.telegram.bot.times.utils.unrecognizedValue
import by.jprof.telegram.bot.times.utils.yourTimeZone
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class TimeZoneCommandUpdateProcessor(
    private val timeZoneDAO: TimeZoneDAO,
    private val bot: RequestsExecutor,
    private val timeZoneParser: TimeZoneParser = TimeZoneParser.DEFAULT,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(TimeZoneCommandUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        timeZoneParser(update)?.let { timeZone ->
            logger.info("TimeZone requested: {}", timeZone)

            when (timeZone.value) {
                TimeZoneValue.Unrecognized -> replyToUnrecognizedTimeZoneRequest(timeZone)
                TimeZoneValue.Empty -> replyToEmptyTimeZoneRequest(timeZone)
                else -> setTimeZone(timeZone)
            }
        }
    }

    private suspend fun replyToUnrecognizedTimeZoneRequest(timeZoneRequest: TimeZoneRequest) {
        bot.reply(to = timeZoneRequest.request, text = unrecognizedValue(), parseMode = MarkdownV2)
    }

    private suspend fun replyToEmptyTimeZoneRequest(timeZoneRequest: TimeZoneRequest) {
        val timeZone = timeZoneDAO.get(timeZoneRequest.user.id.chatId, timeZoneRequest.chat.id.chatId)

        bot.reply(to = timeZoneRequest.request, text = yourTimeZone(timeZone), parseMode = MarkdownV2)
    }

    private suspend fun setTimeZone(timeZoneRequest: TimeZoneRequest) {
        val timeZone = TimeZone(
            user = timeZoneRequest.user.id.chatId,
            username = timeZoneRequest.user.username?.username,
            chat = timeZoneRequest.chat.id.chatId,
        ).run {
            when (timeZoneRequest.value) {
                is TimeZoneValue.Id -> copy(zoneId = timeZoneRequest.value.id)
                is TimeZoneValue.Offset -> copy(offset = timeZoneRequest.value.offset)
                else -> this
            }
        }

        timeZoneDAO.save(timeZone)

        bot.reply(to = timeZoneRequest.request, text = timeZoneSet(timeZone), parseMode = MarkdownV2)
    }
}
