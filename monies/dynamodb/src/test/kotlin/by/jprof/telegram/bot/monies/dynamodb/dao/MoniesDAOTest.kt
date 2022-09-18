package by.jprof.telegram.bot.monies.dynamodb.dao


import by.jprof.telegram.bot.monies.model.Money
import by.jprof.telegram.bot.monies.model.Monies
import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
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
internal class MoniesDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: MoniesDAO

    @BeforeAll
    internal fun setup() {
        sut = MoniesDAO(dynamoDB, "monies")
    }

    @Test
    fun save() = runBlocking {
        sut.save(monies)
    }

    @Test
    fun get() = runBlocking {
        Assertions.assertEquals(monies, sut.get(1, 2))
    }

    @Test
    fun getUnexisting() = runBlocking {
        Assertions.assertNull(sut.get(-1, -2))
    }

    private val monies
        get() = Monies(
            user = 1L,
            chat = 2L,
            monies = mapOf(
                Money.PINS to 3
            )
        )
}
