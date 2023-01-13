package by.jprof.telegram.bot.shop.payload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("rich")
data class RichPayload(
    val status: String,
    val chat: Long,
) : Payload
