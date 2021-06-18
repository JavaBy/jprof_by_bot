package by.jprof.telegram.bot.achievements.editor

import by.jprof.telegram.bot.core.UpdateProcessor
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.types.message.abstracts.FromUserMessage
import dev.inmo.tgbotapi.types.update.abstracts.BaseEditMessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import org.apache.logging.log4j.LogManager

class EditorAchievementUpdateProcessor(
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(EditorAchievementUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        @Suppress("NAME_SHADOWING")
        val update = (update as? BaseEditMessageUpdate) ?: return
        val message = (update.data as? FromUserMessage) ?: return

        logger.debug("{} edited a message", message.user.id.chatId)
    }
}
