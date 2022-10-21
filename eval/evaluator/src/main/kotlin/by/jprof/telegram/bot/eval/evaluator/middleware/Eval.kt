package by.jprof.telegram.bot.eval.evaluator.middleware

import by.jprof.telegram.bot.eval.dto.EvalEvent
import by.jprof.telegram.bot.eval.dto.EvalResponse

interface Eval {
    suspend fun eval(payload: EvalEvent): EvalResponse?
}
