package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

internal class PLNTest {
    private val sut = PLN()

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
            yield(Arguments.of("10000PLN", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("10000EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10000 PLN", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("10000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10.000 PLN", setOf(MonetaryAmount(10.0, "PLN"))))
            yield(Arguments.of("10.000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10K PLN", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("10K EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10 K PLN", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("10      K       PLN", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("PLN 10000", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("EUR 10000", emptySet<MonetaryAmount>()))
            yield(Arguments.of("PLN 10K", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("EUR 10K", emptySet<MonetaryAmount>()))
            yield(Arguments.of("PLN          10K", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("PLN          123456.789", setOf(MonetaryAmount(123456.789, "PLN"))))
            yield(Arguments.of("10000 zl", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("10000 zł", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("10000 ZŁ", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("10000 злотых", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("10000 зл", setOf(MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("1 злотый", setOf(MonetaryAmount(1.0, "PLN"))))
            yield(Arguments.of("5000-10000 PLN", setOf(MonetaryAmount(5000.0, "PLN"), MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("5000 —   10000 PLN", setOf(MonetaryAmount(5000.0, "PLN"), MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("5-10К PLN", setOf(MonetaryAmount(5000.0, "PLN"), MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("5-10    К   PLN", setOf(MonetaryAmount(5000.0, "PLN"), MonetaryAmount(10000.0, "PLN"))))
            yield(Arguments.of("PLN 5000 —   10000", setOf(MonetaryAmount(5000.0, "PLN"), MonetaryAmount(10000.0, "PLN"))))
        }.asStream()
    }
}
