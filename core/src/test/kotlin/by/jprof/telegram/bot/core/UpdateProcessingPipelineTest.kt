package by.jprof.telegram.bot.core

import dev.inmo.tgbotapi.types.update.abstracts.UnknownUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonNull
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTimeout
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class UpdateProcessingPipelineTest {
    @Test
    fun process() {
        val completionFlags = (1..5).map { AtomicBoolean(false) }
        val sut = UpdateProcessingPipeline(
            processors = completionFlags.mapIndexed { index, atomicBoolean ->
                Delay((index + 1) * 1000L, atomicBoolean)
            },
            timeout = 10_000,
        )

        assertTimeout(Duration.ofMillis(6000)) {
            sut.process(UnknownUpdate(1L, "", JsonNull))
        }
        assertTrue(completionFlags.all { it.get() })
    }

    @Test
    fun processWithBroken() {
        val completionFlags = (1..5).map { AtomicBoolean(false) }
        val sut = UpdateProcessingPipeline(
            processors = completionFlags.mapIndexed { index, atomicBoolean ->
                when (index % 2) {
                    0 -> Delay((index + 1) * 1000L, atomicBoolean)
                    else -> Fail()
                }
            },
            timeout = 10_000,
        )

        assertTimeout(Duration.ofMillis(6000)) {
            sut.process(UnknownUpdate(1L, "", JsonNull))
        }
        completionFlags.forEachIndexed { index, atomicBoolean ->
            if (index % 2 == 0) {
                assertTrue(atomicBoolean.get())
            }
        }
    }

    @Test
    fun processWithTimeout() {
        val completionFlags = (1..6).map { AtomicBoolean(false) }
        val sut = UpdateProcessingPipeline(
            processors = completionFlags.mapIndexed { index, atomicBoolean ->
                when (index % 3) {
                    0 -> Delay(Long.MAX_VALUE, atomicBoolean)
                    1 -> Delay(Random.nextLong(0, 100), atomicBoolean)
                    else -> Fail()
                }
            },
            timeout = 1000,
        )

        assertTimeout(Duration.ofMillis(2000)) {
            sut.process(UnknownUpdate(1L, "", JsonNull))
        }
        completionFlags.forEachIndexed { index, atomicBoolean ->
            if (index % 3 == 1) {
                assertTrue(atomicBoolean.get())
            }
            when (index % 3) {
                1 -> assertTrue(atomicBoolean.get())
                else -> assertFalse(atomicBoolean.get())
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
