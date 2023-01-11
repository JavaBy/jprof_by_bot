package by.jprof.telegram.bot.english.dictionaryapi_dev

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.encodeURLPathPart
import io.ktor.serialization.kotlinx.json.json
import java.io.Closeable
import kotlinx.serialization.json.Json

class KtorDictionaryAPIDotDevClient(
    private val baseUrl: String = "https://api.dictionaryapi.dev/api/v2"
) : DictionaryAPIDotDevClient, Closeable {
    private val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    override suspend fun define(term: String): Collection<Word> =
        client.get {
            url("$baseUrl/entries/en/${term.encodeURLPathPart()}")
        }.body()

    override fun close() {
        client.close()
    }
}
