package by.jprof.telegram.bot.currencies.parser

class PLN : MonetaryAmountParserBase() {
    override val currency: String = "PLN"

    override val currencyRegex: String = "(PLN|ZL|ZŁ|ЗЛ)"
}
