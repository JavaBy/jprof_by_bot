package by.jprof.telegram.bot.core

import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.logging.log4j.LogManager

class UpdateProcessingPipeline(
    private val processors: List<UpdateProcessor>,
    private val timeout: Long,
) {
    companion object {
        private val logger = LogManager.getLogger(UpdateProcessingPipeline::class.java)!!
    }

    fun process(update: Update) = runBlocking {
        withTimeoutOrNull(timeout) {
            supervisorScope {
                processors
                    .map {
                        launch(exceptionHandler(it)) {
                            logger.debug("Processing update with ${it::class.simpleName}")
                            it.process(update)
                        }
                    }
                    .joinAll()
            }
        }
    }

    private fun exceptionHandler(updateProcessor: UpdateProcessor) = CoroutineExceptionHandler { _, exception ->
        logger.error("{} failed!", updateProcessor::class.simpleName, exception)
    }
}
