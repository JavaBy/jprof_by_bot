package by.jprof.telegram.bot.shop

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.shop.payload.RichPayload
import by.jprof.telegram.bot.shop.provider.ChatProviderTokens
import by.jprof.telegram.bot.shop.utils.notAShop
import by.jprof.telegram.bot.shop.utils.richInvoiceDescription
import by.jprof.telegram.bot.shop.utils.richInvoiceTitle
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

private val item = LabeledPrice("Флекс × 9000", 500)

@OptIn(PreviewFeature::class)
class RichCommandUpdateProcessor(
    private val bot: RequestsExecutor,
    private val providerTokens: ChatProviderTokens,
    private val json: Json,
) : UpdateProcessor {
    override suspend fun process(update: Update) {
        val message = update.asMessageUpdate()?.data?.asContentMessage() ?: return
        val content = message.content.asTextContent() ?: return
        val command = content.textSources
            .mapNotNull { it.asBotCommandTextSource() }
            .firstOrNull { it.command == "rich" || it.command == "vip" } ?: return
        val status = when (command.command) {
            "rich" -> "I AM RICH"
            else -> "V.I.P."
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
                title = richInvoiceTitle(status),
                description = richInvoiceDescription(),
                payload = json.encodeToString(RichPayload(status = status, chat = message.chat.id.chatId)),
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
