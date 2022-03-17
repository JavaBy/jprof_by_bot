package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

internal class UZSTest {
    private val sut = UZS()

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
            yield(Arguments.of("10000UZS", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("10000EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10000 UZS", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("10000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10.000 UZS", setOf(MonetaryAmount(10.0, "UZS"))))
            yield(Arguments.of("10.000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10K UZS", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("10K EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10 K UZS", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("10      K       UZS", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("UZS 10000", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("EUR 10000", emptySet<MonetaryAmount>()))
            yield(Arguments.of("UZS 10K", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("EUR 10K", emptySet<MonetaryAmount>()))
            yield(Arguments.of("UZS          10K", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("UZS          123456.789", setOf(MonetaryAmount(123456.789, "UZS"))))
            yield(Arguments.of("10000 So'm", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("10000 сўм", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("10000 сум", setOf(MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("5000-10000 UZS", setOf(MonetaryAmount(5000.0, "UZS"), MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("5000 —   10000 UZS", setOf(MonetaryAmount(5000.0, "UZS"), MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("5-10К UZS", setOf(MonetaryAmount(5000.0, "UZS"), MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("5-10    К   UZS", setOf(MonetaryAmount(5000.0, "UZS"), MonetaryAmount(10000.0, "UZS"))))
            yield(Arguments.of("UZS 5000 —   10000", setOf(MonetaryAmount(5000.0, "UZS"), MonetaryAmount(10000.0, "UZS"))))
        }.asStream()
    }
}
