package by.jprof.telegram.bot.english

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.english.dictionaryapi_dev.DictionaryAPIDotDevClient
import by.jprof.telegram.bot.english.dictionaryapi_dev.Meaning
import by.jprof.telegram.bot.english.dictionaryapi_dev.Phonetic
import by.jprof.telegram.bot.english.dictionaryapi_dev.Word
import by.jprof.telegram.bot.english.language_rooms.dao.LanguageRoomDAO
import by.jprof.telegram.bot.english.language_rooms.model.Language
import by.jprof.telegram.bot.english.urban_dictionary.Definition
import by.jprof.telegram.bot.english.urban_dictionary.UrbanDictionaryClient
import by.jprof.telegram.bot.english.utils.iVeExplainedSomeWordsForYou
import by.jprof.telegram.bot.english.utils.noExplanations
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.asBaseMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.BoldTextSource
import dev.inmo.tgbotapi.types.message.textsources.CodeTextSource
import dev.inmo.tgbotapi.types.message.textsources.ItalicTextSource
import dev.inmo.tgbotapi.types.message.textsources.UnderlineTextSource
import dev.inmo.tgbotapi.types.message.textsources.bold
import dev.inmo.tgbotapi.types.message.textsources.italic
import dev.inmo.tgbotapi.types.message.textsources.link
import dev.inmo.tgbotapi.types.message.textsources.regular
import dev.inmo.tgbotapi.types.message.textsources.underline
import dev.inmo.tgbotapi.types.update.abstracts.Update
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.time.withTimeoutOrNull
import org.apache.logging.log4j.LogManager

class ExplainerUpdateProcessor(
    private val languageRoomDAO: LanguageRoomDAO,
    private val urbanDictionaryClient: UrbanDictionaryClient,
    private val dictionaryapiDevClient: DictionaryAPIDotDevClient,
    private val bot: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(ExplainerUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update) {
        val update = update.asBaseMessageUpdate() ?: return
        val roomId = update.data.chat.id
        val message = update.data.asContentMessage() ?: return
        val content = message.content.asTextContent() ?: return

        if (
            languageRoomDAO.get(roomId.chatId, roomId.threadId)?.takeIf { it.language == Language.ENGLISH } == null
        ) {
            return
        }

        val emphasizedWords = extractEmphasizedWords(content)

        logger.debug("Emphasized words: $emphasizedWords")

        val explanations = fetchExplanations(emphasizedWords)

        logger.debug("Explanations: $explanations")

        explanations.keys.sorted().forEach { word ->
            val dictionaryDotDevExplanations = explanations.dictionaryDotDev[word]
            val urbanDictionaryExplanations = explanations.urbanDictionary[word]

            if ((!dictionaryDotDevExplanations.isNullOrEmpty()) || (!urbanDictionaryExplanations.isNullOrEmpty())) {
                bot.reply(
                    to = message,
                    text = buildString {
                        appendLine(regular(iVeExplainedSomeWordsForYou()).markdownV2)
                        appendLine()

                        dictionaryDotDevExplanations(dictionaryDotDevExplanations)
                        urbanDictionaryExplanations(urbanDictionaryExplanations)
                    },
                    parseMode = MarkdownV2,
                    disableWebPagePreview = true,
                )
            } else {
                bot.reply(
                    to = message,
                    text = buildString {
                        append(regular(noExplanations()))
                        append(italic(word))
                    },
                    parseMode = MarkdownV2,
                    disableWebPagePreview = true,
                )
            }
        }
    }

    private fun extractEmphasizedWords(content: TextContent) = content.textSources
        .filter {
            it is BoldTextSource
                || it is CodeTextSource
                || it is ItalicTextSource
                || it is UnderlineTextSource

        }
        .map { it.source }

    private data class Explanations(
        val urbanDictionary: Map<String, Collection<Definition>>,
        val dictionaryDotDev: Map<String, Collection<Word>>,
    ) {
        val keys by lazy {
            urbanDictionary.keys + dictionaryDotDev.keys
        }
    }

    private suspend fun fetchExplanations(terms: Collection<String>) = supervisorScope {
        val urbanDictionaryDefinitions = terms.map { word ->
            asyncWithTimeout(Duration.ofSeconds(5)) {
                word to urbanDictionaryClient.define(word)
            }
        }
        val dictionaryDevDefinitions = terms.map { word ->
            asyncWithTimeout(Duration.ofSeconds(5)) {
                word to dictionaryapiDevClient.define(word)
            }
        }

        Explanations(
            urbanDictionary = urbanDictionaryDefinitions.await().toMap(),
            dictionaryDotDev = dictionaryDevDefinitions.await().toMap(),
        )
    }

    private suspend fun <T> CoroutineScope.asyncWithTimeout(
        timeout: Duration,
        block: suspend CoroutineScope.() -> T,
    ) = async {
        withTimeoutOrNull(timeout) { block() }
    }

    private suspend fun <T> List<Deferred<T>>.await() = this.mapNotNull {
        try {
            it.await()
        } catch (_: Exception) {
            null
        }
    }

    private fun StringBuilder.dictionaryDotDevExplanations(dictionaryDotDevExplanations: Collection<Word>?) {
        dictionaryDotDevExplanations?.let { definitions ->
            definitions.take(3).forEachIndexed { index, definition ->
                val link = definition.sourceUrls?.firstOrNull()

                if (link != null) {
                    append(bold(link(definition.word, link)).markdownV2)
                } else {
                    append(bold(definition.word).markdownV2)
                }
                append(regular(" @ ").markdownV2)
                append(link("Free Dictionary API", "https://dictionaryapi.dev").markdownV2)
                appendLine()
                appendLine()

                definition.phonetics?.takeUnless(Collection<Phonetic>::isEmpty)?.let { phonetics ->
                    appendLine(
                        phonetics.mapNotNull { phonetic ->
                            val text = phonetic.text?.takeUnless(String::isNullOrBlank)
                            val audio = phonetic.audio?.takeUnless(String::isNullOrBlank)

                            when {
                                text != null && audio != null -> link("\uD83D\uDDE3️ $text", audio).markdownV2
                                text != null -> regular(text).markdownV2
                                audio != null -> link("\uD83D\uDDE3️", audio).markdownV2
                                else -> null
                            }
                        }.joinToString(regular(" • ").markdownV2)
                    )
                }

                definition.meanings?.takeUnless(Collection<Meaning>::isEmpty)?.take(3)?.toList()?.let { meanings ->
                    appendLine()

                    meanings.forEachIndexed { meaningIndex, meaning ->
                        appendLine(underline(meaning.partOfSpeech).markdownV2)

                        meaning.definitions.toList().let { definitions ->
                            definitions.forEachIndexed { definitionIndex, definition ->
                                append(regular("\uD83D\uDC49 ").markdownV2)
                                append(regular(definition.definition).markdownV2)

                                definition.example?.let { example ->
                                    appendLine()
                                    append(regular("✍️ ").markdownV2)
                                    append(regular(example).markdownV2)
                                }

                                if (definitionIndex != definitions.lastIndex) {
                                    appendLine()
                                    appendLine()
                                }
                            }
                        }

                        if (meaningIndex != meanings.lastIndex) {
                            appendLine()
                            appendLine()
                        }
                    }
                }
            }
        }
    }

    private fun StringBuilder.urbanDictionaryExplanations(urbanDictionaryExplanations: Collection<Definition>?) {
        urbanDictionaryExplanations?.let {
            if (this.lines().size > 3) {
                appendLine()
                appendLine()
            }

            val topDefinitions = it.sortedBy(Definition::thumbsUp).take(3)

            topDefinitions.forEachIndexed { index, definition ->
                append(bold(link(definition.word, definition.permalink)).markdownV2)
                append(regular(" @ ").markdownV2)
                append(link("Urban Dictionary", "https://urbandictionary.com").markdownV2)
                appendLine()
                appendLine()

                append(regular("\uD83D\uDC49 ").markdownV2)
                append(regular(definition.definition).markdownV2)
                appendLine()

                append(regular("✍️ ").markdownV2)
                append(regular(definition.example).markdownV2)

                if (index != topDefinitions.lastIndex) {
                    appendLine()
                    appendLine()
                }
            }
        }
    }
}
