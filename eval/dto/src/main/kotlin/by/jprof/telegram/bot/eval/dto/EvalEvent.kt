package by.jprof.telegram.bot.eval.dto

import kotlinx.serialization.Serializable

@Serializable
data class EvalEvent(
    val code: String,
)
