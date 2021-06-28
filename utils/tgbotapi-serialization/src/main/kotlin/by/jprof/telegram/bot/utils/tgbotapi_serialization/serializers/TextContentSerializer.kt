package by.jprof.telegram.bot.utils.tgbotapi_serialization.serializers

import by.jprof.telegram.bot.utils.tgbotapi_serialization.surrogates.TextContentSurrogate
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class TextContentSerializer : KSerializer<TextContent> {
    override val descriptor: SerialDescriptor = TextContentSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: TextContent) {
        val surrogate = TextContentSurrogate(
            text = value.text,
            textSources = value.textSources,
        )

        encoder.encodeSerializableValue(TextContentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): TextContent {
        val surrogate = decoder.decodeSerializableValue(TextContentSurrogate.serializer())

        return TextContent(
            text = surrogate.text,
            textSources = surrogate.textSources,
        )
    }
}
