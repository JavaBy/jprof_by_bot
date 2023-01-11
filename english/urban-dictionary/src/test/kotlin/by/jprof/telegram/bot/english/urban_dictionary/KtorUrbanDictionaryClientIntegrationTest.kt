package by.jprof.telegram.bot.english.urban_dictionary

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Tag("it")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KtorUrbanDictionaryClientIntegrationTest {
    private lateinit var sut: KtorUrbanDictionaryClient

    @BeforeAll
    internal fun setup() {
        sut = KtorUrbanDictionaryClient()
    }

    @Test
    fun define() = runBlocking {
        val definitions = sut.define("motherfucker")

        assertTrue(definitions.isNotEmpty())
    }

    @Test
    fun random() = runBlocking {
        val definitions = sut.random()

        assertTrue(definitions.isNotEmpty())
    }
}
