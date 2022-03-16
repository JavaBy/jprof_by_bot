package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

internal class GBPTest {
    private val sut = GBP()

    @ParameterizedTest
    @MethodSource
    fun parse(message: String, expected: Set<MonetaryAmount>) {
        assertEquals(expected, sut(message))
    }

    companion object {
        @JvmStatic
        fun parse(): Stream<Arguments> = sequence<Arguments> {
            yield(Arguments.of("", emptySet<MonetaryAmount>()))
            yield(Arguments.of("test", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10000GBP", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("10000EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10000 GBP", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("10000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10.000 GBP", setOf(MonetaryAmount(10.0, "GBP"))))
            yield(Arguments.of("10.000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10K GBP", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("10K EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10 K GBP", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("10      K       GBP", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("GBP 10000", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("EUR 10000", emptySet<MonetaryAmount>()))
            yield(Arguments.of("GBP 10K", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("EUR 10K", emptySet<MonetaryAmount>()))
            yield(Arguments.of("GBP          10K", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("GBP          123456.789", setOf(MonetaryAmount(123456.789, "GBP"))))
            yield(Arguments.of("10000 £", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("10000 фунтов", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("10000 pounds", setOf(MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("5000-10000 GBP", setOf(MonetaryAmount(5000.0, "GBP"), MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("5000 —   10000 GBP", setOf(MonetaryAmount(5000.0, "GBP"), MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("5-10К GBP", setOf(MonetaryAmount(5000.0, "GBP"), MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("5-10    К   GBP", setOf(MonetaryAmount(5000.0, "GBP"), MonetaryAmount(10000.0, "GBP"))))
            yield(Arguments.of("GBP 5000 —   10000", setOf(MonetaryAmount(5000.0, "GBP"), MonetaryAmount(10000.0, "GBP"))))
        }.asStream()
    }
}
