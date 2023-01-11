package by.jprof.telegram.bot.english.dictionaryapi_dev

interface DictionaryAPIDotDevClient {
    suspend fun define(term: String): Collection<Word>
}
