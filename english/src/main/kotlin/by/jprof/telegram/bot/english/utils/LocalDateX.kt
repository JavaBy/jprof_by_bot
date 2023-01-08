package by.jprof.telegram.bot.english.utils

import java.time.LocalDate

infix fun LocalDate.downTo(other: LocalDate): Iterator<LocalDate> = iterator {
    var current = this@downTo

    while (current >= other) {
        yield(current)
        current = current.minusDays(1)
    }
}
