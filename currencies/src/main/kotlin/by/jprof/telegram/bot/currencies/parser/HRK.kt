package by.jprof.telegram.bot.currencies.parser

class HRK : MonetaryAmountParserBase() {
    override val currency: String = "HRK"

    override val currencyRegex: String = "(HRK|KN|KUNA|KUN|КУН)"
}
