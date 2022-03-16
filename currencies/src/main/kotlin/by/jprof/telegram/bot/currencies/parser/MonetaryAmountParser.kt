package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount

typealias MonetaryAmountParser = (String) -> Set<MonetaryAmount>
