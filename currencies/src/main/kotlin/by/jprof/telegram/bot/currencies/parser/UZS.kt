package by.jprof.telegram.bot.currencies.parser

class UZS : MonetaryAmountParserBase() {
    override val currency: String = "UZS"

    override val currencyRegex: String = "(UZS|SO'M|SOM|СУМ|СЎМ)"
}
