package by.jprof.telegram.bot.herald.model

import kotlinx.serialization.Serializable

@Serializable
data class Frontmatter(
    val chats: List<Long>,
    val image: String? = null,
    val votes: List<String>? = null,
    val disableWebPagePreview: Boolean = true,
)
