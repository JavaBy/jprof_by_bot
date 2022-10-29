package by.jprof.telegram.bot.times

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.times.timezones.dao.TimeZoneDAO
import by.jprof.telegram.bot.times.timezones.model.TimeZone
import by.jprof.telegram.bot.times.utils.mentionDateTime
import by.jprof.telegram.bot.times.utils.messageDateTime
import dev.inmo.tgbotapi.CommonAbstracts.FromUser
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asBotCommandTextSource
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asMentionTextSource
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.extensions.utils.asTextMentionTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.abstracts.PossiblyReplyMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.apache.logging.log4j.LogManager

@OptIn(PreviewFeature::class)
class TimeCommandUpdateProcessor(
    private val timeZoneDAO: TimeZoneDAO,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(TimeCommandUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        @Suppress("NAME_SHADOWING") val update = update.asBaseMessageUpdate() ?: return
        val message = update.data.asContentMessage() ?: return
        val text = message.content.asTextContent() ?: return

        if (text.textSources.mapNotNull { it.asBotCommandTextSource() }.none { it.command == "time" }) {
            return
        }

        val reply = """
            ${replyText(message)}
            
            ${mentionsText(text, message)}
        """.trimIndent().trim()

        bot.reply(to = message, text = reply, parseMode = MarkdownV2ParseMode)
    }

    private suspend fun replyText(message: Message): String {
        @Suppress("NAME_SHADOWING") val message = (message as? PossiblyReplyMessage)?.replyTo ?: return ""
        val author = (message as? FromUser)?.user ?: return ""
        val timeZone = timeZoneDAO.get(author.id.chatId, message.chat.id.chatId) ?: return ""
        val messageTime = Instant.ofEpochMilli(message.date.unixMillisLong).toLocalDateTime(timeZone) ?: return ""
        val now = Instant.now().toLocalDateTime(timeZone) ?: return ""

        logger.info(
            "Replying to {}. Author: {}. TimeZone: {}. Message time: {}, current time: {}", message, author, timeZone, messageTime, now
        )

        return messageDateTime(messageTime, now)
    }

    private suspend fun mentionsText(text: TextContent, message: Message): String {
        val mentions = text.textSources.mapNotNull { it.asMentionTextSource() }
        val textMentions = text.textSources.mapNotNull { it.asTextMentionTextSource() }
        val mentionsWithTimeZones = mentions.mapNotNull {
            val timeZone = timeZoneDAO.getByUsername(it.source, message.chat.id.chatId)

            if (timeZone == null) {
                null
            } else {
                it.source to timeZone
            }
        }
        val textMentionsWithTimeZones = textMentions.mapNotNull {
            val timeZone = timeZoneDAO.get(it.user.id.chatId, message.chat.id.chatId)

            if (timeZone == null) {
                null
            } else {
                it.source to timeZone
            }
        }
        val allMentions = mentionsWithTimeZones + textMentionsWithTimeZones
        val now = Instant.now()

        logger.info(
            "Mentions: {}. Text mentions: {}. Combined mentions with TimeZones: {}", mentions, textMentions, allMentions
        )

        return allMentions.mapNotNull { (who, timeZone) ->
            val userTime = now.toLocalDateTime(timeZone)

            mentionDateTime(who, userTime ?: return@mapNotNull null)
        }.joinToString("\n")
    }

    private fun Instant.toLocalDateTime(timeZone: TimeZone): LocalDateTime? =
        if (timeZone.zoneId != null) {
            this.atZone(ZoneId.of(timeZone.zoneId)).toLocalDateTime()
        } else if (timeZone.offset != null) {
            this.atOffset(ZoneOffset.ofTotalSeconds(timeZone.offset!!)).toLocalDateTime()
        } else {
            null
        }
}
