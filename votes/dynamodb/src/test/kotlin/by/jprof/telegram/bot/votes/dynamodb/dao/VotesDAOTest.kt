package by.jprof.telegram.bot.votes.dynamodb.dao

import by.jprof.telegram.bot.votes.model.Votes
import kotlinx.coroutines.runBlocking
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.common.AWSEndpoint
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
internal class VotesDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: VotesDAO

    @BeforeAll
    internal fun setup() {
        sut = VotesDAO(dynamoDB, "votes")
    }

    @Test
    fun save() = runBlocking {
        sut.save(votes)
    }

    @Test
    fun get() = runBlocking {
        Assertions.assertEquals(votes, sut.get("test"))
    }

    @Test
    fun getUnexisting() = runBlocking {
        Assertions.assertNull(sut.get("unexisting"))
    }

    private val votes
        get() = Votes(
                id = "test",
                options = listOf("+", "-"),
                votes = mapOf(
                        "User1" to "+",
                        "User2" to "-",
                )
        )

    class Endpoint : AWSEndpoint {
        override fun url(): String {
            return System.getenv("DYNAMODB_URL")
        }

        override fun region(): String {
            return System.getenv("DYNAMODB_REGION")
        }

        override fun accessKey(): String {
            return System.getenv("DYNAMODB_ACCESS_KEY")
        }

        override fun secretKey(): String {
            return System.getenv("DYNAMODB_SECRET_KEY")
        }
    }
}
