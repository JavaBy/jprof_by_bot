package by.jprof.telegram.bot.dialogs.model

import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForOptions
import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForQuestion
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

interface DialogState {
    companion object {
        val serializers = SerializersModule {
            polymorphic(DialogState::class) {
                subclass(WaitingForQuestion::class)
                subclass(WaitingForOptions::class)
            }
        }
    }

    val chatId: Long

    val userId: Long
}
