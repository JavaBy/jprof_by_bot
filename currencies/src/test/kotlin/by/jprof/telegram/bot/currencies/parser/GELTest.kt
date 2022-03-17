package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

internal class GELTest {
    private val sut = GEL()

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
            yield(Arguments.of("10000GEL", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("10000EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10000 GEL", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("10000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10.000 GEL", setOf(MonetaryAmount(10.0, "GEL"))))
            yield(Arguments.of("10.000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10K GEL", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("10K EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10 K GEL", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("10      K       GEL", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("GEL 10000", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("EUR 10000", emptySet<MonetaryAmount>()))
            yield(Arguments.of("GEL 10K", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("EUR 10K", emptySet<MonetaryAmount>()))
            yield(Arguments.of("GEL          10K", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("GEL          123456.789", setOf(MonetaryAmount(123456.789, "GEL"))))
            yield(Arguments.of("10000 ₾", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("10000 ლ", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("10000 лари", setOf(MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("5000-10000 GEL", setOf(MonetaryAmount(5000.0, "GEL"), MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("5000 —   10000 GEL", setOf(MonetaryAmount(5000.0, "GEL"), MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("5-10К GEL", setOf(MonetaryAmount(5000.0, "GEL"), MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("5-10    К   GEL", setOf(MonetaryAmount(5000.0, "GEL"), MonetaryAmount(10000.0, "GEL"))))
            yield(Arguments.of("GEL 5000 —   10000", setOf(MonetaryAmount(5000.0, "GEL"), MonetaryAmount(10000.0, "GEL"))))
        }.asStream()
    }
}
