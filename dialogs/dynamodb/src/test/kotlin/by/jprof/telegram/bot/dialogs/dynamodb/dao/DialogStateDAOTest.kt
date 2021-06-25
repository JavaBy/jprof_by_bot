package by.jprof.telegram.bot.dialogs.dynamodb.dao

import by.jprof.telegram.bot.dialogs.model.quizoji.WaitingForQuestion
import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DialogStateDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: DialogStateDAO

    @BeforeAll
    internal fun setup() {
        sut = DialogStateDAO(dynamoDB, "dialog-states")
    }

    @Test
    fun get() = runBlocking {
        Assertions.assertEquals(
            WaitingForQuestion(
                chatId = 1,
                userId = 1,
            ),
            sut.get(1, 1)
        )
    }

    @Test
    fun save() = runBlocking {
        sut.save(
            WaitingForQuestion(
                chatId = 1,
                userId = 2,
            )
        )

        Assertions.assertEquals(
            WaitingForQuestion(
                chatId = 1,
                userId = 2,
            ),
            sut.get(1, 2)
        )
    }

    @Test
    fun delete() = runBlocking {
        sut.save(
            WaitingForQuestion(
                chatId = 1,
                userId = 3,
            )
        )
        sut.delete(1, 3)

        withTimeout(3_000) {
            while (null != sut.get(1, 3)) {
                delay(100)
            }
        }
    }

    @Test
    fun getUnexisting() = runBlocking {
        Assertions.assertNull(sut.get(0, 0))
    }
}
