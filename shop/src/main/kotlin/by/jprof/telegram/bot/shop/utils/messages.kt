package by.jprof.telegram.bot.shop.utils

import java.text.MessageFormat

private val notAShopMessages = listOf(
    "В этом чате ничего не продаётся\\!"
)

internal fun notAShop(): String {
    return notAShopMessages.random()
}

private val richInvoiceTitleMessages = listOf(
    "Статус \"{0}\""
)

fun richInvoiceTitle(status: String): String =
    MessageFormat(richInvoiceTitleMessages.random())
        .format(status)

private val richInvoiceDescriptionMessages = listOf(
    "Отображается в чате возле ваших сообщений"
)

fun richInvoiceDescription(): String =
    richInvoiceDescriptionMessages.random()

private val supportInvoiceTitleMessages = listOf(
    "Поддержка JProf",
)

fun supportInvoiceTitle(): String =
    supportInvoiceTitleMessages.random()

private val supportInvoiceDescriptionMessages = listOf(
    "Безвозмездная поддержка JProf"
)

fun supportInvoiceDescription(): String =
    supportInvoiceDescriptionMessages.random()

private val forwardedPaymentsAreNotSupportedMessages = listOf(
    "Ты пытаешься оплатить пересланный инвойс\\. Покупка и оплата возможны только под оригинальным сообщением\\."
)

internal fun forwardedPaymentsAreNotSupported(): String {
    return forwardedPaymentsAreNotSupportedMessages.random()
}

private val tooManyPinsMessages = listOf(
    "У тебя и так хватает пинов\\!",
    "У тебя и так много пинов\\!",
)

internal fun tooManyPins(): String {
    return tooManyPinsMessages.random()
}
