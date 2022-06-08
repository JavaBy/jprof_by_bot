package by.jprof.telegram.bot.eval.evaluator.middleware

import by.jprof.telegram.bot.eval.dto.EvalEvent
import by.jprof.telegram.bot.eval.dto.EvalResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.apache.logging.log4j.LogManager

class EvalPipeline(
    private val evals: List<Eval>
) {
    companion object {
        private val logger = LogManager.getLogger(EvalPipeline::class.java)!!
    }

    fun process(evalEvent: EvalEvent): EvalResponse = runBlocking {
        supervisorScope {
            evals
                .map { async(exceptionHandler(it)) { it.eval(evalEvent) } }
                .awaitAll()
                .filterNotNull()
                .firstOrNull() ?: EvalResponse.Unsuccessful
        }
    }

    private fun exceptionHandler(eval: Eval) = CoroutineExceptionHandler { _, exception ->
        logger.error("{} failed!", eval::class.simpleName, exception)
    }
}
