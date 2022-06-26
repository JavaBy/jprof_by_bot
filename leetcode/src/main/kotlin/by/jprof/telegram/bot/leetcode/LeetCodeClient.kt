package by.jprof.telegram.bot.leetcode

interface LeetCodeClient {
    suspend fun questionData(slug: String): Question?
}
