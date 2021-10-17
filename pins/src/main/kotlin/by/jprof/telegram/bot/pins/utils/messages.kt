package by.jprof.telegram.bot.pins.utils

import java.text.ChoiceFormat
import java.text.MessageFormat

private val helpMessages = listOf(
    """Эту команду нужно вызывать реплаем на какое\-нибудь сообщение\.
        |
        |Закрепление стоит 1 пин в час\, c округлением вверх\.
        |Админы могут откреплять сообщения до истечения срока\.
        |Пины списываются в момент закрепления сообщения и не возвращаются при досрочном откреплении\.
        |
        |Продолжительность закрепления по умолчанию\: 1 час\.
        |Можно задать другую продолжительность\, указав её после после команды `/pin`\, например\: `/pin 2 часа 30 минут` или `/pin 1 day`\.
        |
        |За каждый реплай к закреплённому сообщению закрепителю начисляется 1 пин\.
        |
        |У тебя {0}
    """.trimMargin(),
)

internal fun help(pins: Int, message: Int? = null): String {
    val messageFormatPattern = if (message != null) {
        helpMessages[message]
    } else {
        helpMessages.random()
    }
    val messageFormat = MessageFormat(messageFormatPattern)
    val choiceFormat = ChoiceFormat(
        doubleArrayOf(
            0.0,
            1.0,
            2.0,
            5.0,
            10.0,
            50.0,
        ),
        arrayOf(
            "нет пинов \uD83E\uDD7A",
            "1 пин \uD83D\uDE10",
            "{0,number,#} пина \uD83D\uDE42", // 2, 3, 4
            "{0,number,#} пинов \uD83D\uDE09", // 5+
            "{0,number,#} пинов \uD83D\uDE0E", // 10+
            "{0,number,#} пинов \uD83E\uDD11", // richbitch
        )
    )

    messageFormat.formats = arrayOf(choiceFormat)

    return messageFormat.format(arrayOf(pins))
}

private val unrecognizedDurationMessages = listOf(
    "Не могу понять желаемую продолжительность\\."
)

internal fun unrecognizedDuration(): String {
    return unrecognizedDurationMessages.random()
}

private val negativeDurationMessages = listOf(
    "Продолжительность слишком маленькая\\! Как так вышло\\?\\!",
)

internal fun negativeDuration(): String {
    return negativeDurationMessages.random()
}

private val tooPositiveDurationMessages = listOf(
    "Продолжительность слишком большая\\!",
    "Прожить бы столько\\!",
    "Я так далеко вперёд не планирую\\!",
    "Тебе пинов\\-то не жалко\\?",
    "Ой\\, да я забуду открепить потом\\. Давай поменьше\\, а\\?",
)

internal fun tooPositiveDuration(): String {
    return tooPositiveDurationMessages.random()
}

private val beggarMessages = listOf(
    "У тебя {0}, а {1}",
)

internal fun beggar(pins: Int, price: Int, message: Int? = null): String {
    val messageFormatPattern = if (message != null) {
        beggarMessages[message]
    } else {
        beggarMessages.random()
    }
    val messageFormat = MessageFormat(messageFormatPattern)
    val balanceFormat = ChoiceFormat(
        doubleArrayOf(
            0.0,
            1.0,
            2.0,
            5.0,
        ),
        arrayOf(
            "нет пинов",
            "1 пин",
            "{0,number,#} пина",
            "{0,number,#} пинов",
        )
    )
    val priceFormat = ChoiceFormat(
        doubleArrayOf(
            1.0,
            2.0,
            5.0,
        ),
        arrayOf(
            "нужен 1 пин",
            "нужно {1,number,#} пина",
            "нужно {1,number,#} пинов",
        )
    )

    messageFormat.formats = arrayOf(balanceFormat, priceFormat)

    return messageFormat.format(arrayOf(pins, price))
}

private val tooManyPinnedMessagesMessages = listOf(
    "Слишком много закреплённых сообщений\\!",
)

internal fun tooManyPinnedMessages(): String {
    return tooManyPinnedMessagesMessages.random()
}
