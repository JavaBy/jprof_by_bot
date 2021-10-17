package by.jprof.telegram.bot.pins

import by.jprof.telegram.bot.pins.model.PinDuration
import by.jprof.telegram.bot.pins.model.PinRequest
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PinRequestTest {
    @ParameterizedTest
    @MethodSource
    fun price(pinRequest: PinRequest, expected: Int) {
        Assertions.assertEquals(expected, pinRequest.price)
    }

    @Test
    fun priceForUnrecognizedDuration() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            PinRequest(mockk(), mockk(), mockk(), mockk(), PinDuration.Unrecognized).price
        }
    }

    fun price() = Stream.of(
        Arguments.of(PinRequest(mockk(), mockk(), mockk(), mockk(), PinDuration.Recognized(Duration.ofSeconds(0))), 0),
        Arguments.of(PinRequest(mockk(), mockk(), mockk(), mockk(), PinDuration.Recognized(Duration.ofSeconds(1))), 1),
        Arguments.of(PinRequest(mockk(), mockk(), mockk(), mockk(), PinDuration.Recognized(Duration.ofSeconds(3599))), 1),
        Arguments.of(PinRequest(mockk(), mockk(), mockk(), mockk(), PinDuration.Recognized(Duration.ofSeconds(3600))), 1),
        Arguments.of(PinRequest(mockk(), mockk(), mockk(), mockk(), PinDuration.Recognized(Duration.ofSeconds(3601))), 2),
        Arguments.of(PinRequest(mockk(), mockk(), mockk(), mockk(), PinDuration.Recognized(Duration.ofSeconds(86400))), 24),
    )
}
