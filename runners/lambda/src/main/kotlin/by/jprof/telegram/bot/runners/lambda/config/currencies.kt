package by.jprof.telegram.bot.runners.lambda.config

import by.jprof.telegram.bot.currencies.parser.CAD
import by.jprof.telegram.bot.currencies.parser.GBP
import by.jprof.telegram.bot.currencies.parser.GEL
import by.jprof.telegram.bot.currencies.parser.HRK
import by.jprof.telegram.bot.currencies.parser.MonetaryAmountParser
import by.jprof.telegram.bot.currencies.parser.MonetaryAmountParsingPipeline
import by.jprof.telegram.bot.currencies.parser.PLN
import by.jprof.telegram.bot.currencies.parser.UZS
import by.jprof.telegram.bot.currencies.parser.BYN 
import by.jprof.telegram.bot.currencies.rates.ExchangeRateClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val currenciesModule = module {
    single<MonetaryAmountParser>(named("PLN")) { PLN() }
    single<MonetaryAmountParser>(named("HRK")) { HRK() }
    single<MonetaryAmountParser>(named("CAD")) { CAD() }
    single<MonetaryAmountParser>(named("GEL")) { GEL() }
    single<MonetaryAmountParser>(named("UZS")) { UZS() }
    single<MonetaryAmountParser>(named("GBP")) { GBP() }
    single<MonetaryAmountParser>(named("BYN")) { BYN() }

    single {
        MonetaryAmountParsingPipeline(
            parsers = getAll(),
        )
    }

    single {
        ExchangeRateClient()
    }
}
