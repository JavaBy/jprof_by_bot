package by.jprof.telegram.bot.times.utils

import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.time.LocalDateTime

class LocalDateTimeFormat : Format() {
    override fun format(obj: Any, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
        val date = (obj as? LocalDateTime) ?: throw IllegalArgumentException()

        toAppendTo.append("%1$02d".format(date.dayOfMonth))
        toAppendTo.append(".")
        toAppendTo.append("%1$02d".format(date.monthValue))
        toAppendTo.append(".")
        toAppendTo.append("%1$04d".format(date.year))
        toAppendTo.append(" ")
        toAppendTo.append("%1$02d".format(date.hour))
        toAppendTo.append(":")
        toAppendTo.append("%1$02d".format(date.minute))

        return toAppendTo
    }

    override fun parseObject(source: String?, pos: ParsePosition): Any? = null
}
