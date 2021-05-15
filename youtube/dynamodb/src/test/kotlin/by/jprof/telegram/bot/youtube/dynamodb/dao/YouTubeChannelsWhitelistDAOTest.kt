package by.jprof.telegram.bot.youtube.dynamodb.dao

import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import by.jprof.telegram.bot.youtube.model.YouTubeChannel
import kotlinx.coroutines.runBlocking
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class YouTubeChannelsWhitelistDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: YouTubeChannelsWhitelistDAO

    @BeforeAll
    internal fun setup() {
        sut = YouTubeChannelsWhitelistDAO(dynamoDB, "youtube-channels-whitelist")
    }


    @Test
    fun get() = runBlocking {
        Assertions.assertEquals(youTubeChannel, sut.get("test"))
    }

    private val youTubeChannel
        get() = YouTubeChannel(
            id = "test",
        )
}
