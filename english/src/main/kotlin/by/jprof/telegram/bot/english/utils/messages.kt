package by.jprof.telegram.bot.english.utils

private val iVeExplainedSomeWordsForYouMessages = listOf(
    "Yo, I've explained some words for you \uD83D\uDC47",
    "Here are some explanations \uD83D\uDC47",
    "You've emphasized some words, so I decided to explain them \uD83D\uDC47",
)

internal fun iVeExplainedSomeWordsForYou(): String {
    return iVeExplainedSomeWordsForYouMessages.random()
}
