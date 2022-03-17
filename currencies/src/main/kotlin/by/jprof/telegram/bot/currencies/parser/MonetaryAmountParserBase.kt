package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount

abstract class MonetaryAmountParserBase : MonetaryAmountParser {
    companion object {
        private const val RANGE = "(-|–|—|―|‒|\\.\\.|\\.\\.\\.|…)"
    }

    override fun invoke(message: String): Set<MonetaryAmount> {
        return (
            listOf(r1, r2)
                .flatMap { it.findAll(message) }
                .mapNotNull {
                    val rawAmount = it.groups["amount"]?.value ?: return@mapNotNull null
                    val isK = it.groups["K"] != null
                    val isM = it.groups["M"] != null
                    val amount = rawAmount.toDouble().run { if (isK) this * 1000 else if (isM) this * 1000000 else this }

                    MonetaryAmount(amount, currency)
                } + listOf(r3, r4)
                .flatMap { it.findAll(message) }
                .mapNotNull {
                    val rawAmount1 = it.groups["amount1"]?.value ?: return@mapNotNull null
                    val rawAmount2 = it.groups["amount2"]?.value ?: return@mapNotNull null
                    val isK = it.groups["K1"] != null || it.groups["K2"] != null
                    val isM = it.groups["M1"] != null || it.groups["M2"] != null

                    val amount1 = rawAmount1.toDouble().run { if (isK) this * 1000 else if (isM) this * 1000000 else this }
                    val amount2 = rawAmount2.toDouble().run { if (isK) this * 1000 else if (isM) this * 1000000 else this }

                    listOf(MonetaryAmount(amount1, currency), MonetaryAmount(amount2, currency))
                }.flatten()
            ).toSet()
    }

    protected abstract val currency: String

    protected abstract val currencyRegex: String

    private fun amount(index: Int? = null): String = "(?<amount${index ?: ""}>(\\d+\\.\\d+)|(\\d+))( *(?<K${index ?: ""}>[КK])|(?<M${index ?: ""}>[МM]))?"

    private val r1 get() = "${amount()} *$currencyRegex".toRegex()

    private val r2 get() = "$currencyRegex *${amount()}".toRegex()

    private val r3 get() = "${amount(1)} *$RANGE *${amount(2)} *$currencyRegex".toRegex()

    private val r4 get() = "$currencyRegex *${amount(1)} *$RANGE *${amount(2)}".toRegex()

    private fun String.toRegex() = this.toRegex(RegexOption.IGNORE_CASE)
}
