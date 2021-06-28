package by.jprof.telegram.bot.utils.tgbotapi_serialization

import by.jprof.telegram.bot.utils.tgbotapi_serialization.serializers.TextContentSerializer
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object TgBotAPI {
    val module = SerializersModule {
        polymorphic(MessageContent::class) {
            // subclass(AnimationContent::class, AnimationContentSerializer())
            // subclass(AudioContent::class, AudioContentSerializer())
            // subclass(AudioMediaGroupContent::class, AudioMediaGroupContentSerializer())
            // subclass(ContactContent::class, ContactContentSerializer())
            // subclass(DiceContent::class, DiceContentSerializer())
            // subclass(DocumentContent::class, DocumentContentSerializer())
            // subclass(DocumentMediaGroupContent::class, DocumentMediaGroupContentSerializer())
            // subclass(GameContent::class, GameContentSerializer())
            // subclass(InvoiceContent::class, InvoiceContentSerializer())
            // subclass(LocationContent::class, LocationContentSerializer())
            // subclass(MediaCollectionContent::class, MediaCollectionContentSerializer())
            // subclass(MediaContent::class, MediaContentSerializer())
            // subclass(MediaGroupContent::class, MediaGroupContentSerializer())
            // subclass(PhotoContent::class, PhotoContentSerializer())
            // subclass(PollContent::class, PollContentSerializer())
            // subclass(StickerContent::class, StickerContentSerializer())
            subclass(TextContent::class, TextContentSerializer())
            // subclass(VenueContent::class, VenueContentSerializer())
            // subclass(VideoContent::class, VideoContentSerializer())
            // subclass(VideoNoteContent::class, VideoNoteContentSerializer())
            // subclass(VisualMediaGroupContent::class, VisualMediaGroupContentSerializer())
            // subclass(VoiceContent::class, VoiceContentSerializer())
        }
    }
}
