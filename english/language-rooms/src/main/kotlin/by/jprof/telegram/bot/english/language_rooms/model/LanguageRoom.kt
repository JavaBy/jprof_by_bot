package by.jprof.telegram.bot.english.language_rooms.model

enum class Language {
    ENGLISH,
}

enum class Violence {
    POLITE,
    MOTHERFUCKER,
}

data class LanguageRoom(
    val chatId: Long,
    val threadId: Long?,
    val language: Language,
    val violence: Violence,
    val urbanWordOfTheDay: Boolean,
)
