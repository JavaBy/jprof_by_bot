package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.shop.payload.Payload
import by.jprof.telegram.bot.shop.payload.RichPayload
import by.jprof.telegram.bot.shop.payload.SupportPayload
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {
    val json = Json {
        encodeDefaults = false
        ignoreUnknownKeys = true
    }

    val s = json.encodeToString<Payload>(RichPayload(status = "status", chat = 123))

    println(json.decodeFromString<SupportPayload>(s))
}
