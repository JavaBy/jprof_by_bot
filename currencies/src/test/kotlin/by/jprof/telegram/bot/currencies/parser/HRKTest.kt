package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

internal class HRKTest {
    private val sut = HRK()

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
            yield(Arguments.of("10000HRK", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("10000EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10000 HRK", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("10000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10.000 HRK", setOf(MonetaryAmount(10.0, "HRK"))))
            yield(Arguments.of("10.000 EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10K HRK", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("10K EUR", emptySet<MonetaryAmount>()))
            yield(Arguments.of("10 K HRK", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("10      K       HRK", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("HRK 10000", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("EUR 10000", emptySet<MonetaryAmount>()))
            yield(Arguments.of("HRK 10K", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("EUR 10K", emptySet<MonetaryAmount>()))
            yield(Arguments.of("HRK          10K", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("HRK          123456.789", setOf(MonetaryAmount(123456.789, "HRK"))))
            yield(Arguments.of("10000 Kn", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("10000 куна", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("10000 кун", setOf(MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("5000-10000 HRK", setOf(MonetaryAmount(5000.0, "HRK"), MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("5000 —   10000 HRK", setOf(MonetaryAmount(5000.0, "HRK"), MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("5-10К HRK", setOf(MonetaryAmount(5000.0, "HRK"), MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("5-10    К   HRK", setOf(MonetaryAmount(5000.0, "HRK"), MonetaryAmount(10000.0, "HRK"))))
            yield(Arguments.of("HRK 5000 —   10000", setOf(MonetaryAmount(5000.0, "HRK"), MonetaryAmount(10000.0, "HRK"))))
        }.asStream()
    }
}
