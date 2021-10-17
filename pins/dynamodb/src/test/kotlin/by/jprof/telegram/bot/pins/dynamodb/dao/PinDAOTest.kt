package by.jprof.telegram.bot.pins.dynamodb.dao

import by.jprof.telegram.bot.pins.model.Pin
import by.jprof.telegram.bot.utils.aws_junit5.Endpoint
import kotlinx.coroutines.runBlocking
import me.madhead.aws_junit5.common.AWSClient
import me.madhead.aws_junit5.dynamo.v2.DynamoDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Tag("db")
@ExtendWith(DynamoDB::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PinDAOTest {
    @AWSClient(endpoint = Endpoint::class)
    private lateinit var dynamoDB: DynamoDbAsyncClient
    private lateinit var sut: PinDAO

    @BeforeAll
    internal fun setup() {
        sut = PinDAO(dynamoDB, "pins")
    }

    @Test
    fun save() = runBlocking {
        sut.save(Pin(-1, -1, -1))
    }

    @Test
    fun get() = runBlocking {
        assertEquals(Pin(1, 1, 1), sut.get(1, 1))
    }

    @Test
    fun findByChatId() = runBlocking {
        assertEquals(setOf(Pin(1, 1, 1), Pin(1, 2, 2), Pin(1, 3, 2)), sut.findByChatId(1).toSet())
    }

    @Test
    fun findByUserId() = runBlocking {
        assertEquals(setOf(Pin(1, 2, 2), Pin(1, 3, 2)), sut.findByUserId(2).toSet())
    }

    @Test
    fun getUnexisting() = runBlocking {
        assertEquals(null, sut.get(-2, -2))
        assertEquals(emptyList<Pin>(), sut.findByUserId(-2))
    }

    @Test
    fun delete() = runBlocking {
        sut.save(Pin(0, 0, 0))
        assertNotNull(sut.get(0, 0))
        sut.delete(0, 0)
        assertNull(sut.get(0, 0))
    }
}
