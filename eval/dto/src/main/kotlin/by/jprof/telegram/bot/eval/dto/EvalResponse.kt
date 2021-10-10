package by.jprof.telegram.bot.eval.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class EvalResponse {
    @Serializable
    data class Successful(
        val language: Language,
        val stdout: String? = null,
        val stderr: String? = null,
    ) : EvalResponse()

    @Serializable
    object Unsuccessful : EvalResponse()
}
