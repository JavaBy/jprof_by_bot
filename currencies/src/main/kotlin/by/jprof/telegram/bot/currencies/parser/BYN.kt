package by.jprof.telegram.bot.currencies.parser

class BYN : MonetaryAmountParserBase() {
    override val currency: String = "BYN"

    override val currencyRegex: String = "(BYN|РУБ|РУБЛЕЙ)"
}
