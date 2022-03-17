package by.jprof.telegram.bot.currencies.parser

class GBP : MonetaryAmountParserBase() {
    override val currency: String = "GBP"

    override val currencyRegex: String = "(GBP|£|POUND|ФУНТ|ФУНТОВ)"
}
