package by.jprof.telegram.bot.launchers.lambda.config

import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.dsl.module
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient

@ExperimentalSerializationApi
val secretsModule = module {
    single {
        SecretsManagerClient.create()
    }
}
