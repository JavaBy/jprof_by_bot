package by.jprof.telegram.bot.english.urban_dictionary

interface UrbanDictionaryClient {
    suspend fun define(term: String): Collection<Definition>

    suspend fun random(): Collection<Definition>
}
