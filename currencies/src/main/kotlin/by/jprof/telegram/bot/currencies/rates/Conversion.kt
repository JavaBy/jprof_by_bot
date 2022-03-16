package by.jprof.telegram.bot.currencies.rates

import kotlinx.serialization.Serializable

@Serializable
data class Conversion(
    val query: Query,
    val result: Double,
)

@Serializable
data class Query(
    val amount: Double,
    val from: String,
    val to: String,
)
