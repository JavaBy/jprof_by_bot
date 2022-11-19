package by.jprof.telegram.bot.herald.impl

import by.jprof.telegram.bot.herald.model.Post
import by.jprof.telegram.bot.votes.dynamodb.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.requests.abstracts.MultipartFile
import dev.inmo.tgbotapi.types.chat.UsernameChat
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import io.ktor.utils.io.streams.asInput
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.inputStream
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.system.exitProcess
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

suspend fun send(post: Post) {
    val image = post.image()
    val votes = post.votes()

    post.frontmatter.chats.forEach { chat ->
        when {
            image != null -> {
                println("Sending image to $chat")

                bot.sendPhoto(
                    chatId = chat.toChatId(),
                    fileId = image,
                    text = post.content.forChat(chat),
                    parseMode = MarkdownV2,
                    replyMarkup = votes?.toInlineKeyboardMarkup(),
                )
            }

            else -> {
                println("Sending text to $chat")

                bot.sendMessage(
                    chatId = chat.toChatId(),
                    text = post.content.forChat(chat),
                    parseMode = MarkdownV2,
                    replyMarkup = votes?.toInlineKeyboardMarkup(),
                    disableWebPagePreview = post.frontmatter.disableWebPagePreview,
                )
            }
        }
    }
}

private val bot = telegramBot(System.getenv("TOKEN_TELEGRAM_BOT") ?: run {
    println("TOKEN_TELEGRAM_BOT is not set!")
    exitProcess(0)
})

private val votesDAO = VotesDAO(
    DynamoDbAsyncClient
        .builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build(),
    System.getenv("TABLE_VOTES") ?: run {
        println("TABLE_VOTES is not set!")
        exitProcess(0)
    }
)

private fun Post.image(): InputFile? {
    val cwd = Path("").absolute()
    val imagePath = this.frontmatter.image?.let { cwd.resolve(it) }

    return if (imagePath != null && imagePath.isRegularFile()) {
        MultipartFile(
            filename = imagePath.name,
            inputSource = { imagePath.inputStream().asInput() }
        )
    } else {
        null
    }
}

private suspend fun Post.votes(): Votes? {
    return if (this.frontmatter.votes == null) {
        null
    } else {
        val votesId = "HERALD-${this.id}"

        votesDAO.get(votesId) ?: Votes(votesId, this.frontmatter.votes)
    }
}

private suspend fun String.forChat(chatId: Long): String {
    val chat = (bot.getChat(chatId.toChatId()) as? UsernameChat)?.username?.username ?: return this

    return "${this.trimEnd()}\n\n${chat.escapeMarkdownV2Common()}"
}
