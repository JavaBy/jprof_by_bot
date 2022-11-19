package by.jprof.telegram.bot.leetcode

import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup

@Serializable
data class Question(
    val title: String,
    val titleSlug: String,
    val content: String?,
    val isPaidOnly: Boolean,
    val difficulty: String,
    val likes: Int,
    val dislikes: Int,
    val categoryTitle: String,
) {
    fun level(): String = when (this.difficulty) {
        "Easy" -> "\uD83E\uDD64"
        "Medium" -> "\uD83E\uDD14"
        "Hard" -> "\uD83E\uDD18"
        else -> ""
    }

    fun paidIndicator() = if (isPaidOnly) {
        "\uD83E\uDD11"
    } else {
        ""
    }

    fun markdownContent(): String? {
        return try {
            val content = Jsoup.parse(this.content ?: return null)
                .select("p")
                .firstOrNull()
                ?.text()
                ?.takeUnless { it.isBlank() } ?: return null

            ((content.takeIf { it.length < 3000 } ?: (content.take(3000) + "…"))).escapeMarkdownV2Common()
        } catch (_: Exception) {
            null
        }
    }
}
