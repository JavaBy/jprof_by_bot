package by.jprof.telegram.bot.core

import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager

class UpdateProcessingPipeline(
    private val processors: List<UpdateProcessor>
) {
    companion object {
        private val logger = LogManager.getLogger(UpdateProcessingPipeline::class.java)!!
    }

    fun process(update: Update) = runBlocking {
        supervisorScope {
            processors
                .map { launch(exceptionHandler(it)) { it.process(update) } }
                .joinAll()
        }
    }

    private fun exceptionHandler(updateProcessor: UpdateProcessor) = CoroutineExceptionHandler { _, exception ->
        logger.error("{} failed!", updateProcessor::class.simpleName, exception)
    }
}
