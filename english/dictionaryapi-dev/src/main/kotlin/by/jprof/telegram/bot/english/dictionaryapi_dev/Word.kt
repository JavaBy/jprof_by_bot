package by.jprof.telegram.bot.english.dictionaryapi_dev

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val word: String,
    val phonetics: Collection<Phonetic>? = null,
    val meanings: Collection<Meaning>? = null,
    val license: License? = null,
    val sourceUrls: Collection<String>? = null,
)

@Serializable
data class Phonetic(
    val text: String? = null,
    val audio: String? = null,
    val sourceUrl: String? = null,
    val license: License? = null,
)

@Serializable
data class Meaning(
    val partOfSpeech: String,
    val definitions: Collection<Definition>,
    val synonyms: Collection<String>? = null,
    val antonyms: Collection<String>? = null,
)

@Serializable
data class Definition(
    val definition: String,
    val example: String? = null,
    val synonyms: Collection<String>? = null,
    val antonyms: Collection<String>? = null,
)

@Serializable
data class License(
    val name: String,
    val url: String,
)
