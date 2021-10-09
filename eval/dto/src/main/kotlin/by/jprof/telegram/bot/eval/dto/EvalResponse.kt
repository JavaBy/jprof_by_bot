package by.jprof.telegram.bot.eval.dto

import kotlinx.serialization.Serializable

@Serializable
data class EvalResponse(
    val language: Language,
    val stdout: String? = null,
    val stderr: String? = null,
)
