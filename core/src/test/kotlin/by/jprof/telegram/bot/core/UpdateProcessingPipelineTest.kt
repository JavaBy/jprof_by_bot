package by.jprof.telegram.bot.core

import dev.inmo.tgbotapi.types.update.abstracts.UnknownUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

internal class UpdateProcessingPipelineTest {
    @Test
    fun process() {
        val completionFlags = (1..5).map { AtomicBoolean(false) }
        val sut = UpdateProcessingPipeline(
            completionFlags.mapIndexed { index, atomicBoolean ->
                Delay((index + 1) * 1000L, atomicBoolean)
            }
        )

        Assertions.assertTimeout(Duration.ofMillis(6000)) {
            sut.process(UnknownUpdate(1L, "", JsonNull))
        }
        Assertions.assertTrue(completionFlags.all { it.get() })
    }

    @Test
    fun processWithBroken() {
        val completionFlags = (1..5).map { AtomicBoolean(false) }
        val sut = UpdateProcessingPipeline(
            completionFlags.mapIndexed { index, atomicBoolean ->
                when (index % 2) {
                    0 -> Delay((index + 1) * 1000L, atomicBoolean)
                    else -> Fail()
                }
            }
        )

        Assertions.assertTimeout(Duration.ofMillis(6000)) {
            sut.process(UnknownUpdate(1L, "", JsonNull))
        }
        completionFlags.forEachIndexed { index, atomicBoolean ->
            if (index % 2 == 0) {
                Assertions.assertTrue(atomicBoolean.get())
            }
        }
    }
}

internal class Delay(
    private val delay: Long,
    private val completionFlag: AtomicBoolean
) : UpdateProcessor {
    override suspend fun process(update: Update) {
        delay(delay)
        completionFlag.set(true)
    }
}

internal class Fail : UpdateProcessor {
    override suspend fun process(update: Update) {
        throw IllegalArgumentException()
    }
}
