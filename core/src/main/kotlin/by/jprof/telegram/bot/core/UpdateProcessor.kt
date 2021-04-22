package by.jprof.telegram.bot.core

import dev.inmo.tgbotapi.types.update.abstracts.Update

interface UpdateProcessor {
    suspend fun process(update: Update)
}
