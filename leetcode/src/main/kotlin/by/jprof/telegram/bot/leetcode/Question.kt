package by.jprof.telegram.bot.leetcode

import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val title: String,
    val titleSlug: String,
    val content: String,
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

    fun markdownContent(): String = this.content
        .replace("&nbsp;", " ")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace(Regex("<sup>(?<sup>.+?)</sup>")) {
            "^${it.groups["sup"]!!.value}"
        }

        .escapeMarkdownV2Common()

        .replace("<code\\>", "`")
        .replace("</code\\>", "`")

        .replace("<pre\\>", "```")
        .replace("</pre\\>", "```")

        .replace("<strong\\>", "*")
        .replace("</strong\\>", "*")

        .replace("<em\\>", "_")
        .replace("</em\\>", "_")

        .replace("<li\\>", "• ")
        .replace("</li\\>", "")

        .replace("<p\\>", "")
        .replace("</p\\>", "")
        .replace("<ul\\>", "")
        .replace("</ul\\>", "")

        .replace("\\n", "\n")
        .replace("\\t", "\t")
}
