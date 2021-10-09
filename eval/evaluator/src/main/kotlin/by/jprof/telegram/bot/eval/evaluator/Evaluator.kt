package by.jprof.telegram.bot.eval.evaluator

import by.jprof.telegram.bot.eval.dto.EvalEvent
import by.jprof.telegram.bot.eval.dto.EvalResponse
import by.jprof.telegram.bot.eval.dto.Language
import by.jprof.telegram.bot.eval.evaluator.config.jsonModule
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.apache.logging.log4j.LogManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import java.io.InputStream
import java.io.OutputStream

@ExperimentalSerializationApi
@Suppress("unused")
class Evaluator : RequestStreamHandler, KoinComponent {
    companion object {
        private val logger = LogManager.getLogger(Evaluator::class.java)
    }

    init {
        startKoin {
            modules(
                jsonModule
            )
        }
    }

    private val json: Json by inject()

    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val payload = input.bufferedReader().use { it.readText() }

        logger.debug("Payload: {}", payload)

        val evalEvent = json.decodeFromString<EvalEvent>(payload)

        logger.debug("Parsed event: {}", evalEvent)

        val evalResponse = EvalResponse(Language.UNKNOWN)

        output.buffered().use { json.encodeToStream(evalResponse, it) }
    }
}
