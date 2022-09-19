package by.jprof.telegram.bot.times.utils

import by.jprof.telegram.bot.times.timezones.model.TimeZone
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import java.text.MessageFormat

private val unrecognizedValueMessages = listOf(
    "Не могу разобрать таймзону\\!",
    "Не понимаю\\, чё ты тут написал\\!",
    "Не знаю такой таймзоны\\!",
)

internal fun unrecognizedValue(): String {
    return unrecognizedValueMessages.random()
}

private val emptyTimeZoneMessages = listOf(
    "Ты не установил свою таймзону \uD83D\uDE14",
)

private val yourTimeZoneMessages = listOf(
    "Твоя таймзона: {0}\\.",
)

internal fun yourTimeZone(timeZone: TimeZone?): String {
    return if ((null == timeZone) || ((timeZone.zoneId == null) && (timeZone.offset == null))) {
        emptyTimeZoneMessages.random()
    } else {
        MessageFormat(yourTimeZoneMessages.random()).format(
            arrayOf((timeZone.zoneId ?: timeZone.offset).toString().escapeMarkdownV2Common())
        )
    }
}

private val timeZoneSetMessages = listOf(
    "Установлена таймзона: {0}\\.",
)

internal fun timeZoneSet(timeZone: TimeZone?): String {
    return if ((null == timeZone) || ((timeZone.zoneId == null) && (timeZone.offset == null))) {
        emptyTimeZoneMessages.random()
    } else {
        MessageFormat(timeZoneSetMessages.random()).format(
            arrayOf((timeZone.zoneId ?: timeZone.offset).toString().escapeMarkdownV2Common())
        )
    }
}
