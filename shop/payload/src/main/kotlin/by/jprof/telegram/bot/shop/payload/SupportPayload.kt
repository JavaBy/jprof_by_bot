package by.jprof.telegram.bot.shop.payload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("support")
data class SupportPayload(
    val chat: Long,
) : Payload()
