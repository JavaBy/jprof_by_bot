package by.jprof.telegram.bot.jep

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class JsoupJEPSummary : JEPSummary {
    override suspend fun of(jep: String): String? =
        try {
            withContext(Dispatchers.IO) {
                Jsoup
                    .connect("https://openjdk.org/jeps/${jep}")
                    .get()
                    .select("#Summary + p")
                    .first()
                    ?.text()
            }
        } catch (_: Exception) {
            null
        }
}
