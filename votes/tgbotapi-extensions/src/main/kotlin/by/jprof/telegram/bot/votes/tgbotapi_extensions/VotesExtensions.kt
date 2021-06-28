package by.jprof.telegram.bot.votes.tgbotapi_extensions

import by.jprof.telegram.bot.votes.model.Votes
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup

fun Votes.toInlineKeyboardMarkup() = InlineKeyboardMarkup(
    listOf(
        this
            .options
            .map {
                CallbackDataInlineKeyboardButton(
                    text = "${this.count(it)} $it",
                    callbackData = "${this.id}:$it"
                )
            }
    )
)

fun Votes.toInlineKeyboardMarkup(size: Int) = InlineKeyboardMarkup(
    if (this.options.isEmpty()) {
        listOf(emptyList())
    } else {
        this
            .options
            .map {
                CallbackDataInlineKeyboardButton(
                    text = "${this.count(it)} $it",
                    callbackData = "${this.id}:$it"
                )
            }
            .chunked(size)
    }
)
