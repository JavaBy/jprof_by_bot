package by.jprof.telegram.bot.pins.utils

import by.jprof.telegram.bot.pins.model.PinDuration
import by.jprof.telegram.bot.pins.model.PinRequest
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asBotCommandTextSource
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asFromUser
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.message.abstracts.PossiblyReplyMessage
import dev.inmo.tgbotapi.types.message.textsources.TextSource
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import java.time.Duration
import java.time.temporal.ChronoUnit
import org.apache.logging.log4j.LogManager

interface PinRequestFinder : (Update) -> PinRequest? {
    @PreviewFeature
    object DEFAULT : PinRequestFinder {
        private val logger = LogManager.getLogger(DEFAULT::class.java)!!

        private val periods = mapOf(
            ChronoUnit.MONTHS to listOf(
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +month", RegexOption.IGNORE_CASE),
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +monthe?s", RegexOption.IGNORE_CASE), // English, yeah!
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +месяцa?", RegexOption.IGNORE_CASE),
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +месяцев", RegexOption.IGNORE_CASE),
            ),
            ChronoUnit.WEEKS to listOf(
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +weeks?", RegexOption.IGNORE_CASE),
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +недел[яиь]", RegexOption.IGNORE_CASE),
            ),
            ChronoUnit.DAYS to listOf(
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +days?", RegexOption.IGNORE_CASE),
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +день", RegexOption.IGNORE_CASE),
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +дн((я)|(ей))", RegexOption.IGNORE_CASE),
            ),
            ChronoUnit.HOURS to listOf(
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +((h)|(hours?))", RegexOption.IGNORE_CASE),
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +((ч)|(час))", RegexOption.IGNORE_CASE),
            ),
            ChronoUnit.MINUTES to listOf(
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +((min)|(minutes?))", RegexOption.IGNORE_CASE),
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +((м)|(мин)|(минут))", RegexOption.IGNORE_CASE),
            ),
            ChronoUnit.SECONDS to listOf(
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +((sec)|(seconds?))", RegexOption.IGNORE_CASE),
                Regex(" +(?<value>(\\d+)|(\\d+[.,]\\d+)) +((с)|(сек)|(секунд))", RegexOption.IGNORE_CASE),
            ),
        )

        override fun invoke(update: Update): PinRequest? {
            val message = update.asBaseMessageUpdate() ?: return null
            val content = message.data.asContentMessage() ?: return null
            val fromUser = content.asFromUser() ?: return null
            val replyTo = content as? PossiblyReplyMessage
            val text = content.content.asTextContent() ?: return null
            val commandIndex = text
                .textSources
                .mapNotNull { it.asBotCommandTextSource() }
                .indexOfFirst { it.command == "pin" }
                .takeUnless { it < 0 } ?: return null
            val commandArgument = text.textSources.getOrNull(commandIndex + 1)

            return PinRequest(
                message = replyTo?.replyTo,
                user = fromUser.user,
                chat = content.chat,
                request = content,
                duration = parse(commandArgument),
            )
        }

        private fun parse(argument: TextSource?): PinDuration = if (argument == null) {
            PinDuration.Recognized(Duration.ofHours(1))
        } else {
            var result = 0.0

            periods.forEach { (unit, regexps) ->
                val matches = regexps
                    .mapNotNull { it.find(argument.source) }

                if (matches.size > 1) {
                    logger.warn("Found multiple {} matches for {}", unit, argument.source)

                    return@parse PinDuration.Unrecognized
                }

                result += unit.duration.seconds * matches.sumOf { it.groups["value"]!!.value.toDouble() }
            }

            if (result > 0) {
                PinDuration.Recognized(Duration.ofSeconds(result.toLong()))
            } else {
                PinDuration.Unrecognized
            }
        }
    }
}
