package by.jprof.telegram.bot.youtube

import by.jprof.telegram.bot.core.UpdateProcessor
import by.jprof.telegram.bot.votes.dao.VotesDAO
import by.jprof.telegram.bot.votes.model.Votes
import by.jprof.telegram.bot.votes.tgbotapi_extensions.toInlineKeyboardMarkup
import by.jprof.telegram.bot.votes.voting_processor.VotingProcessor
import by.jprof.telegram.bot.youtube.dao.YouTubeChannelsWhitelistDAO
import com.google.api.services.youtube.YouTube
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.formatting.boldMarkdownV2
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.TextLinkTextSource
import dev.inmo.tgbotapi.types.message.textsources.URLTextSource
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager

private fun votesConstructor(votesId: String): Votes = Votes(votesId, listOf("\uD83D\uDC4D", "\uD83D\uDC4E"))

class YouTubeUpdateProcessor(
    private val votesDAO: VotesDAO,
    private val youTubeChannelsWhitelistDAO: YouTubeChannelsWhitelistDAO,
    private val bot: RequestsExecutor,
    private val youTube: YouTube,
) : VotingProcessor(
    "YOUTUBE",
    votesDAO,
    ::votesConstructor,
    bot,
), UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(YouTubeUpdateProcessor::class.java)!!
        private val linkRegex =
            "https?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|(?:be-nocookie|be)\\.com/(?:watch|\\w+\\?(?:feature=\\w+.\\w+&)?v=|v/|e/|embed/|user/(?:[\\w#]+/)+))(?<id>[^&#?\\n]+)".toRegex()
        private const val ACCEPTED_DISPLAY_LEN = 500
    }

    override suspend fun process(update: Update) {
        when (update) {
            is MessageUpdate -> processMessage(update.data)
            is CallbackQueryUpdate -> processCallbackQuery(update.data)
        }
    }

    private suspend fun processMessage(message: Message) {
        val youTubeVideos = extractYoutubeVideos(message) ?: return

        logger.info("Detected {} YouTube videos: {}", youTubeVideos.size, youTubeVideos)

        supervisorScope {
            youTubeVideos
                .map { launch { replyToYouTubeVideo(it, message) } }
                .joinAll()
        }
    }

    private fun extractYoutubeVideos(message: Message): List<String>? =
        (message as? ContentMessage<*>)?.let { contentMessage ->
            (contentMessage.content as? TextContent)?.let { content ->
                content
                    .textSources
                    .mapNotNull {
                        (it as? URLTextSource)?.source ?: (it as? TextLinkTextSource)?.url
                    }
                    .mapNotNull { url ->
                        linkRegex.matchEntire(url)?.groups?.get("id")?.value?.takeUnless { it.isBlank() }
                    }
            }
        }

    private suspend fun replyToYouTubeVideo(video: String, message: Message) {
        val response = withContext(Dispatchers.IO) {
            youTube.videos().list(listOf("snippet", "statistics")).setId(listOf(video)).execute()
        }
        val videoDetails = response.items.firstOrNull() ?: return
        val snippet = videoDetails.snippet
        val channelId = snippet.channelId

        youTubeChannelsWhitelistDAO.get(channelId)?.let { _ ->
            logger.debug("$channelId is in the white list")

            val views = videoDetails.statistics.viewCount
            val likes = videoDetails.statistics.likeCount
            val rawDescription = if (snippet.description.length > ACCEPTED_DISPLAY_LEN) {
                snippet.description.substring(IntRange(0, ACCEPTED_DISPLAY_LEN)) + "…"
            } else {
                snippet.description
            }
            val description = rawDescription.escapeMarkdownV2Common()
            val videoText = "Cast your vote for: ${snippet.title}".boldMarkdownV2() +
                "\n\n```\n$description\n\n```" +
                "Views: $views / Likes: $likes".boldMarkdownV2() //trim indent have strange layout
            val votesId = video.toVotesID()
            val votes = votesDAO.get(votesId) ?: votesConstructor(votesId)

            bot.reply(
                to = message,
                text = videoText,
                parseMode = MarkdownV2,
                replyMarkup = votes.toInlineKeyboardMarkup()
            )
        } ?: run {
            logger.debug("$channelId is not in the white list")
        }
    }
}
