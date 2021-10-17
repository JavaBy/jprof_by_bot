package by.jprof.telegram.bot.monies.model

// It's not a typo, and I know about plurals in English…
data class Monies(
    val user: Long,
    val chat: Long,
    val monies: Map<Money, Int> = emptyMap(),
) {
    val pins: Int?
        get() = monies[Money.PINS]
}
