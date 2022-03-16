package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

internal class CADTest {
    private val sut = CAD()

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
            yield(Arguments.of("10000CAD", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("10000EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10000 CAD", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("10000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10.000 CAD", setOf(MonetaryAmount(10.0, "CAD"))))
            yield(Arguments.of("10.000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10K CAD", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("10K EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10 K CAD", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("10      K       CAD", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("CAD 10000", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("EUR 10000", emptySet<MonetaryAmount>()))
            yield(Arguments.of("CAD 10K", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("EUR 10K", emptySet<MonetaryAmount>()))
            yield(Arguments.of("CAD          10K", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("CAD          123456.789", setOf(MonetaryAmount(123456.789, "CAD"))))
            // yield(Arguments.of("10000 CA$", setOf(MonetaryAmount(10000.0, "CAD"))))
            // yield(Arguments.of("10000 Can$", setOf(MonetaryAmount(10000.0, "CAD"))))
            // yield(Arguments.of("10000 C$", setOf(MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("5000-10000 CAD", setOf(MonetaryAmount(5000.0, "CAD"), MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("5000 —   10000 CAD", setOf(MonetaryAmount(5000.0, "CAD"), MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("5-10К CAD", setOf(MonetaryAmount(5000.0, "CAD"), MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("5-10    К   CAD", setOf(MonetaryAmount(5000.0, "CAD"), MonetaryAmount(10000.0, "CAD"))))
            yield(Arguments.of("CAD 5000 —   10000", setOf(MonetaryAmount(5000.0, "CAD"), MonetaryAmount(10000.0, "CAD"))))
        }.asStream()
    }
}
