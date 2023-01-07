package by.jprof.telegram.bot.english.urban_dictionary_definition_formatter

import by.jprof.telegram.bot.english.urban_word_of_the_day.model.UrbanWordOfTheDay
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common

fun UrbanWordOfTheDay.format(): String = buildString {
    appendLine("*[${word.escapeMarkdownV2Common()}]($permalink)*")
    appendLine()
    appendLine(definition.escapeMarkdownV2Common())
    example?.let {
        appendLine()
        appendLine("_${it.escapeMarkdownV2Common()}_")
    }
}
