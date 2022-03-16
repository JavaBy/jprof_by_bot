package by.jprof.telegram.bot.currencies.parser


class CAD : MonetaryAmountParserBase() {
    override val currency: String = "CAD"

    override val currencyRegex: String = "(CAD|CA$|Can$|C$)"
}
