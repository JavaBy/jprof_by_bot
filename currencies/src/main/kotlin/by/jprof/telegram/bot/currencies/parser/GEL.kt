package by.jprof.telegram.bot.currencies.parser

class GEL : MonetaryAmountParserBase() {
    override val currency: String = "GEL"

    override val currencyRegex: String = "(GEL|₾|ლ|ЛАРИ)"
}

