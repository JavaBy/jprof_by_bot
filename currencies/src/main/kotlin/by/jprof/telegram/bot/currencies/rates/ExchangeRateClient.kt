package by.jprof.telegram.bot.currencies.rates

import by.jprof.telegram.bot.currencies.CurrenciesUpdateProcessor
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import java.io.Closeable

class ExchangeRateClient : Closeable {
    companion object {
        private val logger = LogManager.getLogger(ExchangeRateClient::class.java)!!
    }

    private val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun convert(amount: Double, from: String, to: String): Conversion? = try {
        client.get<Conversion> {
            url("https://api.exchangerate.host/convert")
            parameter("amount", amount)
            parameter("from", from)
            parameter("to", to)
        }
    } catch (e: Exception) {
        logger.error("Conversion exception", e)
        null
    }

    override fun close() {
        client.close()
    }
}
