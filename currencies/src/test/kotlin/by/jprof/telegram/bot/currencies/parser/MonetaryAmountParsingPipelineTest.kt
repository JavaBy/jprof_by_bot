package by.jprof.telegram.bot.currencies.parser

import by.jprof.telegram.bot.currencies.model.MonetaryAmount
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MonetaryAmountParsingPipelineTest {
    @Test
    fun parse() {
        val message = "test"
        val monetaryAmountParser1 = mockk<MonetaryAmountParser> {
            every { this@mockk.invoke(message) } returns setOf(MonetaryAmount(42.0, "EUR"))
        }
        val monetaryAmountParser2 = mockk<MonetaryAmountParser> {
            every { this@mockk.invoke(message) } returns emptySet()
        }
        val monetaryAmountParser3 = mockk<MonetaryAmountParser> {
            every { this@mockk.invoke(message) } throws IllegalArgumentException()
        }
        val sut = MonetaryAmountParsingPipeline(
            parsers = listOf(monetaryAmountParser1, monetaryAmountParser2, monetaryAmountParser3)
        )

        assertEquals(
            listOf(MonetaryAmount(42.0, "EUR")),
            sut.parse(message)
        )

        verify(exactly = 1) { monetaryAmountParser1.invoke(message) }
        verify(exactly = 1) { monetaryAmountParser2.invoke(message) }
        verify(exactly = 1) { monetaryAmountParser3.invoke(message) }
    }
}
