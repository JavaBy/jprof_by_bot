package by.jprof.telegram.bot.launchers.lambda.config

import by.jprof.telegram.bot.shop.provider.ChatProviderTokens
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient

private const val SECRET_PAYMENT_PROVIDER_TOKENS = "jprof-by-bot-secret-payment-provider-tokens"

const val TOKEN_TELEGRAM_BOT = "TOKEN_TELEGRAM_BOT"
const val TOKEN_YOUTUBE_API = "TOKEN_YOUTUBE_API"
const val TABLE_VOTES = "TABLE_VOTES"
const val TABLE_YOUTUBE_CHANNELS_WHITELIST = "TABLE_YOUTUBE_CHANNELS_WHITELIST"
const val TABLE_KOTLIN_MENTIONS = "TABLE_KOTLIN_MENTIONS"
const val TABLE_DIALOG_STATES = "TABLE_DIALOG_STATES"
const val TABLE_QUIZOJIS = "TABLE_QUIZOJIS"
const val TABLE_MONIES = "TABLE_MONIES"
const val TABLE_PINS = "TABLE_PINS"
const val TABLE_TIMEZONES = "TABLE_TIMEZONES"
const val TABLE_LANGUAGE_ROOMS = "TABLE_LANGUAGE_ROOMS"
const val TABLE_URBAN_WORDS_OF_THE_DAY = "TABLE_URBAN_WORDS_OF_THE_DAY"
const val STATE_MACHINE_UNPINS = "STATE_MACHINE_UNPINS"
const val TIMEOUT = "TIMEOUT"

val envModule = module {
    listOf(
        TOKEN_TELEGRAM_BOT,
        TOKEN_YOUTUBE_API,
        TABLE_VOTES,
        TABLE_YOUTUBE_CHANNELS_WHITELIST,
        TABLE_KOTLIN_MENTIONS,
        TABLE_DIALOG_STATES,
        TABLE_QUIZOJIS,
        TABLE_MONIES,
        TABLE_PINS,
        TABLE_TIMEZONES,
        TABLE_LANGUAGE_ROOMS,
        TABLE_URBAN_WORDS_OF_THE_DAY,
        STATE_MACHINE_UNPINS,
    ).forEach { variable ->
        single(named(variable)) {
            System.getenv(variable)!!
        }
    }

    single(named(TIMEOUT)) {
        System.getenv(TIMEOUT)!!.toLong()
    }

    single<ChatProviderTokens> {
        val json: Json = get()
        val secrets: SecretsManagerClient = get()
        val secret = secrets.getSecretValue {
            it.secretId(SECRET_PAYMENT_PROVIDER_TOKENS)
        }

        json.decodeFromString(secret.secretString())
    }
}
