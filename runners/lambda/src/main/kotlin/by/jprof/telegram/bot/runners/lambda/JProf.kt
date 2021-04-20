package by.jprof.telegram.bot.runners.lambda

import by.jprof.telegram.bot.core.UpdateProcessingPipeline
import by.jprof.telegram.bot.runners.lambda.config.envModule
import by.jprof.telegram.bot.runners.lambda.config.jsonModule
import by.jprof.telegram.bot.runners.lambda.config.pipelineModule
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import dev.inmo.tgbotapi.types.update.abstracts.UpdateDeserializationStrategy
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

@Suppress("unused")
class JProf : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>, KoinComponent {
    companion object {
        private val logger = LogManager.getLogger(JProf::class.java)
        private val OK = APIGatewayV2HTTPResponse
                .builder()
                .withStatusCode(200)
                .withHeaders(mapOf(
                        "Content-Type" to "application/json"
                ))
                .withBody("{}")
                .build()
    }

    init {
        startKoin {
            modules(envModule, jsonModule, pipelineModule)
        }
    }

    private val json: Json by inject()
    private val pipeline: UpdateProcessingPipeline by inject()

    override fun handleRequest(input: APIGatewayV2HTTPEvent, context: Context): APIGatewayV2HTTPResponse {
        logger.debug("Incoming request: {}", input)

        val update = json.decodeFromString(UpdateDeserializationStrategy, input.body ?: return OK)

        logger.debug("Parsed update: {}", update)

        pipeline.process(update)

        return OK
    }
}
