package by.jprof.telegram.bot.english.dictionaryapi_dev

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Tag("it")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KtorKtorDictionaryAPIDotDevClientIntegrationTest {
    private lateinit var sut: KtorDictionaryAPIDotDevClient

    @BeforeAll
    internal fun setup() {
        sut = KtorDictionaryAPIDotDevClient()
    }

    @Test
    fun define() = runBlocking {
        val definitions = sut.define("motherfucker")

        assertTrue(definitions.isNotEmpty())
    }
}
