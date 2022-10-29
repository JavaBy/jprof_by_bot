package by.jprof.telegram.bot.launchers.lambda

import by.jprof.telegram.bot.core.UpdateProcessingPipeline
import by.jprof.telegram.bot.launchers.lambda.config.currenciesModule
import by.jprof.telegram.bot.launchers.lambda.config.databaseModule
import by.jprof.telegram.bot.launchers.lambda.config.envModule
import by.jprof.telegram.bot.launchers.lambda.config.jsonModule
import by.jprof.telegram.bot.launchers.lambda.config.pipelineModule
import by.jprof.telegram.bot.launchers.lambda.config.sfnModule
import by.jprof.telegram.bot.launchers.lambda.config.telegramModule
import by.jprof.telegram.bot.launchers.lambda.config.youtubeModule
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import dev.inmo.tgbotapi.types.update.abstracts.UpdateDeserializationStrategy
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

@PreviewFeature
@ExperimentalSerializationApi
@Suppress("unused")
class JProf : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>, KoinComponent {
    companion object {
        private val logger = LogManager.getLogger(JProf::class.java)
        private val OK = APIGatewayV2HTTPResponse
            .builder()
            .withStatusCode(200)
            .withHeaders(
                mapOf(
                    "Content-Type" to "application/json"
                )
            )
            .withBody("{}")
            .build()
    }

    init {
        startKoin {
            modules(
                envModule,
                databaseModule,
                jsonModule,
                telegramModule,
                youtubeModule,
                pipelineModule,
                sfnModule,
                currenciesModule,
            )
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
