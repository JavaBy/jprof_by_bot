package by.jprof.telegram.bot.times.utils

import by.jprof.telegram.bot.times.model.TimeZoneRequest
import by.jprof.telegram.bot.times.model.TimeZoneValue
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asBotCommandTextSource
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asFromUser
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import java.time.ZoneId

interface TimeZoneParser : (Update) -> TimeZoneRequest? {
    @PreviewFeature
    object DEFAULT : TimeZoneParser {
        override fun invoke(update: Update): TimeZoneRequest? {
            val message = update.asBaseMessageUpdate() ?: return null
            val content = message.data.asContentMessage() ?: return null
            val fromUser = content.asFromUser() ?: return null
            val text = content.content.asTextContent() ?: return null
            val commandIndex = text
                .textSources
                .mapNotNull { it.asBotCommandTextSource() }
                .indexOfFirst { it.command == "tz" }
                .takeUnless { it < 0 } ?: return null
            val commandArgument = text.textSources.getOrNull(commandIndex + 1)?.asText?.trim()

            return TimeZoneRequest(
                user = fromUser.user,
                chat = content.chat,
                request = content,
                value = parse(commandArgument),
            )
        }

        private fun parse(argument: String?): TimeZoneValue = if (argument.isNullOrBlank()) {
            TimeZoneValue.Empty
        } else {
            val zoneId = try {
                ZoneId.of(argument, ZoneId.SHORT_IDS)
            } catch (e: Exception) {
                null
            }
            val offset = argument.toIntOrNull()

            when {
                zoneId != null -> TimeZoneValue.Id(zoneId.id)
                offset != null -> TimeZoneValue.Offset(offset)
                else -> TimeZoneValue.Unrecognized
            }
        }
    }
}
