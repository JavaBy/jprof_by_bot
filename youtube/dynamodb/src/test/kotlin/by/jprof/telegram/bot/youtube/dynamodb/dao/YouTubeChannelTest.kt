package by.jprof.telegram.bot.youtube.dynamodb.dao

import by.jprof.telegram.bot.youtube.model.YouTubeChannel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal class YouTubeChannelTest {
    @Test
    fun toYouTubeChannel() {
        Assertions.assertEquals(
            youTubeChannel,
            attributes.toYouTubeChannel()
        )
    }

    private val youTubeChannel
        get() = YouTubeChannel(
            id = "test",
        )
    private val attributes
        get() = mapOf(
            "id" to AttributeValue.builder().s("test").build(),
        )
}
