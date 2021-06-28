package by.jprof.telegram.bot.utils.tgbotapi_serialization.surrogates

import dev.inmo.tgbotapi.types.MessageEntity.textsources.TextSourcesList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TextContent")
internal data class TextContentSurrogate(
    val text: String,
    val textSources: TextSourcesList = emptyList()
)
