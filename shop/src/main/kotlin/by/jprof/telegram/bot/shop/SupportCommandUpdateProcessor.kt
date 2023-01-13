package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.shop.payload.SupportPayload
import by.jprof.telegram.bot.shop.provider.ChatProviderTokens
import by.jprof.telegram.bot.shop.utils.notAShop
import by.jprof.telegram.bot.shop.utils.supportInvoiceDescription
import by.jprof.telegram.bot.shop.utils.supportInvoiceTitle
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.payments.sendInvoice
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.asBotCommandTextSource
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.payments.LabeledPrice
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val item = LabeledPrice("❤️ × ∞", 100)

@OptIn(PreviewFeature::class)
class SupportCommandUpdateProcessor(
    private val bot: RequestsExecutor,
    private val providerTokens: ChatProviderTokens,
    private val json: Json,
) : UpdateProcessor {
    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asContentMessage() ?: return
        val content = message.content.asTextContent() ?: return

        if (content.textSources.mapNotNull { it.asBotCommandTextSource() }.none { it.command == "support" }) {
            return
        }

        val chatProviderToken = providerTokens[message.chat.id.chatId]

        if (chatProviderToken == null) {
            bot.reply(
                to = message,
                text = notAShop(),
                parseMode = MarkdownV2ParseMode,
                disableNotification = true,
            )
        } else {
            bot.sendInvoice(
                chatId = message.chat.id,
                title = supportInvoiceTitle(),
                description = supportInvoiceDescription(),
                payload = json.encodeToString(SupportPayload(chat = message.chat.id.chatId)),
                providerToken = chatProviderToken,
                currency = currency,
                prices = listOf(item),
                startParameter = "forwarded_payment",
                replyToMessageId = message.messageId,
                allowSendingWithoutReply = true,
            )
        }
    }
}
