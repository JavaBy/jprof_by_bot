package by.jprof.telegram.bot.currencies.rates

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import java.io.Closeable
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

class ExchangeRateClient : Closeable {
    companion object {
        private val logger = LogManager.getLogger(ExchangeRateClient::class.java)!!
    }

    private val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun convert(amount: Double, from: String, to: String): Conversion? = try {
        client.get {
            url("https://api.exchangerate.host/convert")
            parameter("amount", amount)
            parameter("from", from)
            parameter("to", to)
        }.body()
    } catch (e: Exception) {
        logger.error("Conversion exception", e)
        null
    }

    override fun close() {
        client.close()
    }
}
