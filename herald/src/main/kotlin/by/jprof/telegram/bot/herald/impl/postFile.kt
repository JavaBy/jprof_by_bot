package by.jprof.telegram.bot.herald.impl

import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.exists

fun postFile(): Path? {
    val cwd = Path("")
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    return cwd.resolve(today.format(formatter) + ".md").absolute().takeIf { it.exists() }
}
