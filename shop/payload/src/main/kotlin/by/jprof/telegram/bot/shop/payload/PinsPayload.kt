package by.jprof.telegram.bot.shop.payload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("pins")
data class PinsPayload(
    val pins: Long,
    val chat: Long,
) : Payload
