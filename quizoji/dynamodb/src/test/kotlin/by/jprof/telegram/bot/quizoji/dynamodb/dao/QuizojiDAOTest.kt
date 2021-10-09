package by.jprof.telegram.bot.quizoji.dynamodb.dao

import by.jprof.telegram.bot.quizoji.model.Quizoji
import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import dev.inmo.tgbotapi.types.MessageEntity.textsources.RegularTextSource
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.runBlocking
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class QuizojiDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: QuizojiDAO

    @BeforeAll
    internal fun setup() {
        sut = QuizojiDAO(dynamoDB, "quizoji")
    }

    @Test
    fun save() = runBlocking {
        sut.save(quizoji)
    }

    @Test
    fun get() = runBlocking {
        Assertions.assertEquals(quizoji, sut.get("test"))
    }

    @Test
    fun getUnexisting() = runBlocking {
        Assertions.assertNull(sut.get("unexisting"))
    }

    private val quizoji
        get() = Quizoji(
            id = "test",
            question = TextContent(
                text = "Choose the door",
                textSources = listOf(RegularTextSource("Choose the door")),
            ),
        )
}
