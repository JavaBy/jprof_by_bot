package by.jprof.telegram.bot.leetcode

private val slugRegex = "https?://leetcode\\.com/problems/(?<slug>[^/]+).*".toRegex()

@Suppress("FunctionName")
fun NaiveRegexSlugExtractor(message: String): String? {
    return slugRegex.matchEntire(message)?.groups?.get("slug")?.value
}
