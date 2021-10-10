package by.jprof.telegram.bot.eval.evaluator.middleware

import by.jprof.telegram.bot.eval.dto.EvalEvent
import by.jprof.telegram.bot.eval.dto.EvalResponse
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration

internal class EvalPipelineTest {
    @Test
    fun processWithBroken() {
        val sut = EvalPipeline(
            (1..5).map { index ->
                when (index % 2) {
                    0 -> Delay((index + 1) * 1000L)
                    else -> Fail()
                }
            }
        )

        Assertions.assertTimeout(Duration.ofMillis(6000)) {
            sut.process(EvalEvent(""))
        }
    }
}

internal class Delay(
    private val delay: Long,
) : Eval {
    override suspend fun eval(payload: EvalEvent): EvalResponse? {
        delay(delay)

        return EvalResponse.Unsuccessful
    }
}

internal class Fail : Eval {
    override suspend fun eval(payload: EvalEvent): EvalResponse? {
        throw IllegalArgumentException()
    }
}
