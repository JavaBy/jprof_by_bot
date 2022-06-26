package by.jprof.telegram.bot.leetcode

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
)
