package by.jprof.telegram.bot.english.urban_dictionary

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import java.io.Closeable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KtorUrbanDictionaryClient(
    private val baseUrl: String = "https://api.urbandictionary.com/v0"
) : UrbanDictionaryClient, Closeable {
    private val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    override suspend fun define(term: String): Collection<Definition> =
        client.get {
            url("$baseUrl/define")
            parameter("term", term)
        }.body<UrbanDictionaryResponse>().list

    override suspend fun random(): Collection<Definition> =
        client.get {
            url("$baseUrl/random")
        }.body<UrbanDictionaryResponse>().list

    override fun close() {
        client.close()
    }

    @Serializable
    private data class UrbanDictionaryResponse(
        val list: List<Definition>
    )
}
